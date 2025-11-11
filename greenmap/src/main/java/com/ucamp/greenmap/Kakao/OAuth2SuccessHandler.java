package com.ucamp.greenmap.Kakao;

import com.ucamp.greenmap.Kakao.repository.UserRepository;
import com.ucamp.greenmap.badge.domain.Badge;
import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.repository.BadgeRepository;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.image.repository.ImageRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.repository.PointRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Bag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final com.ucamp.greenmap.member.repository.MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final PointRepository pointRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        System.out.println("OAuth2 Success Handler 실행됨");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Long kakaoId = oAuth2User.getAttribute("id");
        Map<String, Object> properties = oAuth2User.getAttribute("properties");
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String nickname = properties != null ? (String) properties.get("nickname") : "카카오유저";
        String profileImageUrl = properties != null ? (String) properties.get("profile_image") : null;

        if (profileImageUrl == null) {
            profileImageUrl = "https://em-content.zobj.net/thumbs/120/apple/325/leaf-fluttering-in-wind_1f343.png";
        }

        String principal = Optional.ofNullable(email).orElse("kakao:" + kakaoId);

        // 카카오 ID로 먼저 조회 (더 정확함)
        Member user = memberRepository.findByKakaoId(kakaoId).orElse(null);

        // 카카오 ID로 찾지 못하면 이메일로 조회 (기존 로직 유지)
        if (user == null) {
            user = userRepository.findByEmail(principal).orElse(null);
        }

        if (user == null) {
            // 신규 회원가입
            log.info("신규 카카오 회원가입 시작: kakaoId={}, email={}, nickname={}", kakaoId, principal, nickname);

            Image img = new Image(profileImageUrl);
            imageRepository.save(img);

            user = new Member();
            user.setEmail(principal);
            user.setKakaoId(kakaoId);
            user.setNickname(nickname);
            user.setPassword("SOCIAL_LOGIN");
            user.setImage(img);
            user.setIsActive(true); // 활성 상태로 설정
            user.setCreatedAt(); // 생성 시간 설정

            Member saved = userRepository.save(user);
            log.info("신규 카카오 회원가입 완료: memberId={}", saved.getMemberId());
            Point point = Point.builder()
                    .member(saved)
                    .point(0L)
                    .monthPoint(0L)
                    .usedPoint(0L)
                    .wholePoint(0L)
                    .carbonSaveTotal(0L)
                    .pointTimes(0L)
                    .wholePointTimes(0L)
                    .build();

            pointRepository.save(point);

            List<Badge> allBadges = badgeRepository.findAll();
            List<MemberBadge> memberBadges = new ArrayList<>();

            for (Badge badge : allBadges) {
                MemberBadge mb = MemberBadge.builder()
                        .member(saved)
                        .badge(badge)
                        .progress(0L)
                        .isSelected(false)
                        .build();
                if (badge.getBadgeId() == 1L) { // 최초 가입 배지
                    mb.setIsActive(false);
                    mb.setIsSelected(true);
                }
                memberBadges.add(mb);
            }
            memberBadgeRepository.saveAll(memberBadges);

        } else {
            Image img = user.getImage();
            if (img != null) {
                String currentUrl = img.getImageUrl(); // LAZY 초기화
                if (!currentUrl.equals(profileImageUrl)) {
                    img.setImageUrl(profileImageUrl);
                    imageRepository.save(img);
                }
            }
        }

        // (신규/기존 유저 모두) JWT 발급
        String accessToken = jwtTokenProvider.accessTokenGenerate(
                user.getMemberId(),
                new Date(System.currentTimeMillis() + 1000L * 60 * 60) // 1시간
        );

        // Frontend redirect
        if (!response.isCommitted()) {
            response.sendRedirect(frontendUrl + "/login/success?token=" + accessToken);
            // response.sendRedirect("http://localhost:5173/login/success?token=" +
            // accessToken);
        }
    }
}
