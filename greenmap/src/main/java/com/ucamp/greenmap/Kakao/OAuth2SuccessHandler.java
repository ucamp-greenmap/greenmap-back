package com.ucamp.greenmap.Kakao;

import com.ucamp.greenmap.Kakao.repository.UserRepository;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.image.repository.ImageRepository;
import com.ucamp.greenmap.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // ✅ 카카오 JSON 구조에 맞게 안전하게 꺼내기
        Long kakaoId = oAuth2User.getAttribute("id");

        Map<String, Object> properties = oAuth2User.getAttribute("properties");
        String nickname = properties != null ? (String) properties.get("nickname") : "카카오유저";

        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        // ✅ 이메일 없으면 kakao:{id} 로 처리 (동의 안한 사용자 대비)
        String principal = Optional.ofNullable(email).orElse("kakao:" + kakaoId);

        Member user = userRepository.findByEmail(principal).orElse(null);

        // ✅ 카카오 프로필 이미지 가져오기
        String profileImageUrl = null;
        if (properties != null) {
            profileImageUrl = (String) properties.get("profile_image");
        }

        // ✅ 프로필 이미지 없으면 기본값
        if (profileImageUrl == null) {
            profileImageUrl = "https://em-content.zobj.net/thumbs/120/apple/325/leaf-fluttering-in-wind_1f343.png";
        }

        if (user == null) {
            // ✅ Image 엔티티 저장
            Image img = new Image(profileImageUrl);
            imageRepository.save(img);

            // ✅ Member 저장
            user = new Member();
            user.setEmail(principal);
            user.setKakaoId(kakaoId);
            user.setNickname(nickname);
            user.setPassword("SOCIAL_LOGIN");
            user.setImage(img);

            userRepository.save(user);

        } else {
            // ✅ 기존 유저 로그인 시, 이미지가 변경됐으면 업데이트
            Image img = user.getImage();
            if (!img.getImageUrl().equals(profileImageUrl)) {
                img.setImageUrl(profileImageUrl);
                imageRepository.save(img);
            }
        }

        // ✅ JWT 발급
        String accessToken = jwtTokenProvider.accessTokenGenerate(
                user.getEmail(),
                new Date(System.currentTimeMillis() + 1000L * 60 * 60)
        );

        // ✅ 프론트로 토큰 전달
        response.sendRedirect("http://localhost:5173/login/success?token=" + accessToken);
    }
}
