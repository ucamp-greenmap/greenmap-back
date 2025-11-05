package com.ucamp.greenmap.member.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.member.dto.request.SignUpRequest;
import com.ucamp.greenmap.member.dto.response.SignUpResponse;
import com.ucamp.greenmap.member.service.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class SignUpController {
    private final SignUpService signUpService;

    @PostMapping
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(
            @RequestParam SignUpRequest request){

        SignUpResponse response = signUpService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("일반 회원가입 성공 ",response));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmail(
            @RequestParam("email") String email) {

        boolean exists = signUpService.existsByEmail(email);

        Map<String, Boolean> result = new HashMap<>();
        result.put("state", exists);

        return ResponseEntity.ok(ApiResponse.success("이메일 중복 확인 완료", result));
    }



    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkNickname(
            @RequestParam("nickname") String nickname) {

        boolean exists = signUpService.existsByNickname(nickname);

        Map<String, Boolean> result = new HashMap<>();
        result.put("state", exists);

        return ResponseEntity.ok(ApiResponse.success("닉네임 중복 확인 완료", result));
    }

}