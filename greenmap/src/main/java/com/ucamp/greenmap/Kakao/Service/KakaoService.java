//package com.ucamp.greenmap.Kakao.Service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ucamp.greenmap.Kakao.JwtTokenProvider;
//import com.ucamp.greenmap.Kakao.repository.UserRepository;
//import com.ucamp.greenmap.Kakao.response.LoginResponse;
//import com.ucamp.greenmap.image.domain.Image;
//import com.ucamp.greenmap.image.repository.ImageRepository;
//import com.ucamp.greenmap.member.domain.Member;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Date;
//import java.util.HashMap;
//
//@Service
//@RequiredArgsConstructor
//public class KakaoService {
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private final UserRepository userRepository;
//    private final ImageRepository imageRepository;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Value("${kakao.key.client-id}")
//    private String clientId;
//
//    @Value("${kakao.redirect-uri}")
//    private String redirectUri;
//
//    public String getKakaoLoginUrl() {
//        return "https://kauth.kakao.com/oauth/authorize" +
//                "?client_id=" + clientId +
//                "&redirect_uri=" + redirectUri +
//                "&response_type=code";
//    }
//
//    /** Web 버전 카카오 로그인 **/
//    public LoginResponse kakaoLogin(String code, String currentDomain) {
//        String redirectUri = this.redirectUri;
//
//        // 1️⃣ 인가 코드로 Access Token 요청
//        String accessToken = getAccessToken(code, redirectUri);
//
//        // 2️⃣ Access Token으로 사용자 정보 요청
//        HashMap<String, Object> userInfo = getKakaoUserInfo(accessToken);
//
//        // 3️⃣ 사용자 회원가입 & JWT 발급
//        return kakaoUserLogin(userInfo);
//    }
//
//    // 1. "인가 코드"로 "액세스 토큰" 요청
//    private String getAccessToken(String code, String redirectUri) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", clientId);
//        body.add("redirect_uri", redirectUri);
//        body.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );
//
//        String responseBody = response.getBody();
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            JsonNode jsonNode = objectMapper.readTree(responseBody);
//            return jsonNode.get("access_token").asText();
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("❌ 카카오 토큰 파싱 실패", e);
//        }
//    }
//
//    // 2. 카카오 사용자 정보 요청
//    private HashMap<String, Object> getKakaoUserInfo(String accessToken) {
//        HashMap<String, Object> userInfo = new HashMap<>();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.GET,
//                new HttpEntity<>(headers),
//                String.class
//        );
//
//        String responseBody = response.getBody();
//        logger.info("✅ 카카오 유저 정보 응답: {}", responseBody);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            JsonNode jsonNode = objectMapper.readTree(responseBody);
//            Long id = jsonNode.get("id").asLong();
//            String email = jsonNode.path("kakao_account").path("email").asText(null);
//            String nickname = jsonNode.path("properties").path("nickname").asText(null);
//
//            userInfo.put("id", id);
//            userInfo.put("email", email);
//            userInfo.put("nickname", nickname);
//            return userInfo;
//        } catch (Exception e) {
//            throw new RuntimeException("❌ 카카오 사용자 정보 파싱 실패", e);
//        }
//    }
//
//    // 3. 카카오 회원가입 & JWT 발급
//    private LoginResponse kakaoUserLogin(HashMap<String, Object> userInfo) {
//        Long kakaoId = (Long) userInfo.get("id");
//        String email = (String) userInfo.get("email");
//        String nickname = (String) userInfo.get("nickname");
//
//        String defaultImageUrl = "https://em-content.zobj.net/thumbs/120/apple/325/leaf-fluttering-in-wind_1f343.png";
//
//        // 🔹 DB 조회
//        Member kakaoUser = userRepository.findByEmail(email).orElse(null);
//
//        // 🔹 회원이 없으면 신규 저장
//        if (kakaoUser == null) {
//            Image defaultImage = new Image(defaultImageUrl);
//            imageRepository.save(defaultImage);
//
//            kakaoUser = new Member();
//            kakaoUser.setKakaoId(kakaoId);
//            kakaoUser.setEmail(email);
//            kakaoUser.setNickname(nickname);
//            kakaoUser.setPassword("SOCIAL_LOGIN");
//            kakaoUser.setImage(defaultImage);
//
//            userRepository.save(kakaoUser);
//        }
//
//        // 🔹 JWT 발급
//        String accessToken = jwtTokenProvider.accessTokenGenerate(
//                kakaoUser.getEmail(),
//                new Date(System.currentTimeMillis() + 1000L * 60 * 60) // 1시간
//        );
//
//        String refreshToken = jwtTokenProvider.refreshTokenGenerate(
//                new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14) // 2주
//        );
//
//        // 🔹 프론트에 전달할 응답
//        return LoginResponse.builder()
//                .nickname(kakaoUser.getNickname())
//                .email(kakaoUser.getEmail())
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }
//}
