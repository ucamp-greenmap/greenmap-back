//package com.ucamp.greenmap.Kakao.controller;
//
//import com.ucamp.greenmap.Kakao.Service.KakaoService;
//import com.ucamp.greenmap.Kakao.response.LoginResponse;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/users/login/oauth")
//public class KakaoController {
//
//    private final KakaoService kakaoService;
//
//    @GetMapping("/start")
//    public void redirectToKakao(HttpServletResponse response) throws IOException {
//        String kakaoAuthUrl = kakaoService.getKakaoLoginUrl();
//        response.sendRedirect(kakaoAuthUrl);
//    }
//
//    @GetMapping("/kakao")
//    public LoginResponse kakaoCallback(@RequestParam("code") String code) {
//        System.out.println("인가 코드 수신: " + code);
//        return kakaoService.kakaoLogin(code, "local");
//    }
//    @GetMapping("/kakao")
//    public void kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
//        LoginResponse loginResponse = kakaoService.kakaoLogin(code, "local");
//
//        // 프론트 URL로 리다이렉트 (JWT 전달)
//        String redirectUrl = "http://localhost:3000/login/success"
//                + "?accessToken=" + loginResponse.getAccessToken()
//                + "&refreshToken=" + loginResponse.getRefreshToken();
//
//        response.sendRedirect(redirectUrl);
//    }
//
//        // 프론트 URL로 리다이렉트 (JWT 전달)
//        String redirectUrl = "http://localhost:3000/login/success"
//                + "?accessToken=" + loginResponse.getAccessToken()
//                + "&refreshToken=" + loginResponse.getRefreshToken();
//
//        response.sendRedirect(redirectUrl);
//    }


//}

