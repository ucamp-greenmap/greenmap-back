package com.ucamp.greenmap.member.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.member.dto.request.MemberRequest;
import com.ucamp.greenmap.member.dto.response.MemberResponse;
import com.ucamp.greenmap.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MemberController {

    private final MemberService memberService;

    //내 정보 조회 (JWT 기반)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo(@AuthenticationPrincipal String email) {

        MemberResponse response = memberService.getMyInfo(email);

        return ResponseEntity.ok(ApiResponse.success("회원 정보 조회 성공", response));
    }

    @PutMapping("deactivate")
    public ResponseEntity<MemberResponse> deactivateUser(@AuthenticationPrincipal String email) {
        MemberResponse response = memberService.deactivateUser(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<MemberResponse> updateUser(
            @RequestBody MemberRequest request,
            @AuthenticationPrincipal String email
    ) {
        MemberResponse response = memberService.updateUser(request, email);
        return ResponseEntity.ok(response);
    }



}
