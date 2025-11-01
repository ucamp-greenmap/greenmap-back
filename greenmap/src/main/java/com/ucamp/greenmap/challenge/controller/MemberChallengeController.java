package com.ucamp.greenmap.challenge.controller;

import com.ucamp.greenmap.challenge.dto.request.MemberChallengeDto;
import com.ucamp.greenmap.challenge.dto.response.ChallengeAvailResponse;
import com.ucamp.greenmap.challenge.dto.response.MemberChallengeregis;
import com.ucamp.greenmap.challenge.service.MemberChallengeServcieImpl;
import com.ucamp.greenmap.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chal")
@RequiredArgsConstructor
public class MemberChallengeController {

    private final MemberChallengeServcieImpl memberChallengeServcie;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberChallengeregis>> regisMemChal(
            @RequestBody MemberChallengeDto request,
            @AuthenticationPrincipal Long memberId

    ) {
        MemberChallengeregis response =
                memberChallengeServcie.registMemberChallenge(memberId, request.getChallengeId());

        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<ChallengeAvailResponse>> availChallenge(
            @AuthenticationPrincipal Long memberId
    ) {
        ChallengeAvailResponse response = memberChallengeServcie.availChallenge(memberId);
        return ResponseEntity.ok(ApiResponse.success("참여 가능한 챌린지 조회 성공", response));
    }



}
