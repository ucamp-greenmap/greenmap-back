package com.ucamp.greenmap.Kakao.controller;

import com.ucamp.greenmap.Kakao.Service.KakaoService;
import com.ucamp.greenmap.Kakao.response.LoginResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Api(tags = {"유저 API"})
public class KakaoController{

    private final KakaoService kakaoService;

    //web 버전
    @ResponseBody
    @GetMapping("/login/oauth/kakao")
    @ApiOperation(value = "웹 카카오 로그인", notes = "웹 프론트 버전 카카오 로그인")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam String code, HttpServletRequest request) {
        try {
            // 현재 도메인 확인
            String currentDomain = request.getServerName();
            return ResponseEntity.ok(kakaoService.kakaoLogin(code, currentDomain));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        }
    }
}