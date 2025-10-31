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

        // ✅ 카카오 JSON 구조 가져오기
        Long kakaoId = oAuth2User.getAttribute("id");
        Map<String, Object> properties = oAuth2User.getAttribute("properties");
        Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String nickname = properties != null ? (String) properties.get("nickname") : "카카오유저";
        String profileImageUrl = properties != null ? (String) properties.get("profile_image") : null;

        if (profileImageUrl == null) {
            profileImageUrl = "https://em-content.zobj.net/thumbs/120/apple/325/leaf-fluttering-in-wind_1f343.png";
        }

        // ✅ 이메일 없으면 kakao:{id} 사용
        String principal = Optional.ofNullable(email).orElse("kakao:" + kakaoId);

        // ✅ 기존 유저 조회
        Member user = userRepository.findByEmail(principal).orElse(null);

        // ✅ 신규 유저면 저장
        if (user == null) {
            Image img = new Image(profileImageUrl);
            imageRepository.save(img);

            user = new Member();
            user.setEmail(principal);
            user.setKakaoId(kakaoId);
            user.setNickname(nickname);
            user.setPassword("SOCIAL_LOGIN");
            user.setImage(img);

            userRepository.save(user);
        } else {
            // ✅ 기존유저 프로필 이미지 갱신
            Image img = user.getImage();
            if (img != null && !img.getImageUrl().equals(profileImageUrl)) {
                img.setImageUrl(profileImageUrl);
                imageRepository.save(img);
            }
        }

        // ✅ JWT 발급
        String accessToken = jwtTokenProvider.accessTokenGenerate(
                user.getEmail(),
                new Date(System.currentTimeMillis() + 1000L * 60 * 60) // 1h
        );

        // ✅ 프론트로 리다이렉트 + 토큰 전달
        response.sendRedirect("http://localhost:5173/login/success?token=" + accessToken);
    }
}
