package com.ucamp.greenmap.member.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.member.dto.request.RecodeRequest;
import com.ucamp.greenmap.member.dto.response.MyPageResponse;
import com.ucamp.greenmap.member.dto.response.RecodeResponse;
import com.ucamp.greenmap.member.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public ResponseEntity<ApiResponse<MyPageResponse>> getMyPage(@AuthenticationPrincipal Long memberId) {

        MyPageResponse response = myPageService.getMyPage(memberId);

        return ResponseEntity.ok(ApiResponse.success("마이페이지 조회 성공", response));
    }

    @GetMapping("/recode")
    public ResponseEntity<ApiResponse<RecodeResponse>> getRecode(@AuthenticationPrincipal Long memberId){
        RecodeResponse response = myPageService.getRecode(memberId);
        return ResponseEntity.ok(ApiResponse.success("이번달 기록 조회",response));
    }
}
