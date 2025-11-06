package com.ucamp.greenmap.member.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.member.dto.request.MemberRequest;
import com.ucamp.greenmap.member.dto.response.MemberResponse;
import com.ucamp.greenmap.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    //내 정보 조회 (JWT 기반)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo(@AuthenticationPrincipal Long memberId) {

        log.info("memberId :" + memberId);
        MemberResponse response = memberService.getMyInfo(memberId);

        return ResponseEntity.ok(ApiResponse.success("회원 정보 조회 성공", response));
    }

    @PutMapping("/deactivate")
    public ResponseEntity<ApiResponse<MemberResponse>> deactivateUser(@AuthenticationPrincipal Long memberId) {
        MemberResponse response = memberService.deactivateUser(memberId);
        return ResponseEntity.ok(ApiResponse.success("회원탈퇴 성공",response));
    }

    @PutMapping
    public ResponseEntity<MemberResponse> updateUser(
            @RequestBody MemberRequest request,
            @AuthenticationPrincipal Long memberId
    ) {
        MemberResponse response = memberService.updateUser(request, memberId);
        return ResponseEntity.ok(response);
    }



}
