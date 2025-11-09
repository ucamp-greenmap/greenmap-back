package com.ucamp.greenmap.member.service;

import com.ucamp.greenmap.Kakao.JwtTokenProvider;
import com.ucamp.greenmap.badge.domain.Badge;
import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.repository.BadgeRepository;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.image.repository.ImageRepository;
import com.ucamp.greenmap.member.JwtTokenProviderBasic;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.dto.request.BasicLoginRequest;
import com.ucamp.greenmap.member.dto.request.SignUpRequest;
import com.ucamp.greenmap.member.dto.response.BasicLoginResponse;
import com.ucamp.greenmap.member.dto.response.SignUpResponse;
import com.ucamp.greenmap.member.repository.MemberRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProviderBasic jwtTokenProviderBasic;
    private final ImageRepository imageRepository;
    private final PointRepository pointRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final CategoryRepository categoryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BadgeRepository badgeRepository;

    @Override
    public SignUpResponse signup(SignUpRequest request) {

        //  닉네임 중복 체크 (백엔드 2중 방어)
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        //  이메일 중복 체크 (백엔드 2중 방어)
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        //  비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        //  기본 이미지 설정
        String defaultImage = "https://em-content.zobj.net/thumbs/120/apple/325/leaf-fluttering-in-wind_1f343.png";
        String imageUrl = (request.getImageUrl() != null) ? request.getImageUrl() : defaultImage;

        Image image = Image.builder()
                .imageUrl(imageUrl)
                .build();
        imageRepository.save(image);

        //  회원 저장
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encryptedPassword)
                .nickname(request.getNickname())
                .image(image)
                .build();
        Member saved = memberRepository.save(member);

        //  포인트 초기화
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

        //  기본 배지 지급
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

        //  JWT 발급
        String token = jwtTokenProvider.accessTokenGenerate(member.getMemberId(),
                new Date(System.currentTimeMillis() + 1000L * 60 * 60)
        );

        //  DTO 반환
        return SignUpResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .imageUrl(member.getImage().getImageUrl())
                .accessToken(token)
                .build();
    }

    // 이메일 중복 확인
    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }


    // 닉네임 중복 확인
    @Override
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 로그인
    @Override
    public BasicLoginResponse login(BasicLoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (member.getIsActive() != null && !member.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계정입니다.");
        }

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.accessTokenGenerate(member.getMemberId(),
                new Date(System.currentTimeMillis() + 1000L * 60 * 60)
        );


        return BasicLoginResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .accessToken(token)
                .build();
    }
}
