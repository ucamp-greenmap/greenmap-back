package com.ucamp.greenmap.Kakao;

import com.ucamp.greenmap.Kakao.repository.UserRepository;
import com.ucamp.greenmap.image.domain.Image;
import com.ucamp.greenmap.image.repository.ImageRepository;
import com.ucamp.greenmap.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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

        Member user = userRepository.findByEmail(principal).orElse(null);

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
            response.sendRedirect("http://localhost:5173/login/success?token=" + accessToken);
        }
    }
}
