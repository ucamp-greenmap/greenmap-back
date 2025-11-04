package com.ucamp.greenmap.member.controller;


import com.ucamp.greenmap.Kakao.response.LoginResponse;
import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.member.dto.request.BasicLoginRequest;
import com.ucamp.greenmap.member.dto.response.BasicLoginResponse;
import com.ucamp.greenmap.member.service.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final SignUpService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<BasicLoginResponse>> login(@RequestBody BasicLoginRequest request) {

        BasicLoginResponse response =  loginService.login(request);
        return ResponseEntity.ok(ApiResponse.success("일반로그인",response));
    }


}
