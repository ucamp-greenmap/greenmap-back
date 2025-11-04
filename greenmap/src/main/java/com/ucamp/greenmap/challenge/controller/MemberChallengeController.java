package com.ucamp.greenmap.challenge.controller;

import com.ucamp.greenmap.challenge.dto.request.MemberChallengeDto;
import com.ucamp.greenmap.challenge.dto.request.ProgressUpdateRequest;
import com.ucamp.greenmap.challenge.dto.response.*;
import com.ucamp.greenmap.challenge.service.MemberChallengeServcieImpl;
import com.ucamp.greenmap.common.dto.ApiResponse;
import io.swagger.models.Response;
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
            @AuthenticationPrincipal Long memberId)
    {
        MemberChallengeregis response =
                memberChallengeServcie.registMemberChallenge(memberId, request.getChallengeId());

        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }
    //참여 가능 챌린지
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<ChallengeAvailResponse>> availChallenge(
            @AuthenticationPrincipal Long memberId)
    {
        ChallengeAvailResponse response = memberChallengeServcie.availChallenge(memberId);
        return ResponseEntity.ok(ApiResponse.success("참여 가능한 챌린지 조회 성공", response));
    }
    //참여중인 챌린지
    @GetMapping("/attend")
    public ResponseEntity<ApiResponse<AttendChallengeResponse>> attendChallenge(
            @AuthenticationPrincipal Long memberId)
    {
        AttendChallengeResponse response = memberChallengeServcie.attendChallenge(memberId);
        return ResponseEntity.ok(ApiResponse.success("참여중인 챌린지 조회 성공",response));
    }
    //참여 완료 챌린지
    @GetMapping("/end")
    public ResponseEntity<ApiResponse<EndChallengeResponse>> endChallenge(
            @AuthenticationPrincipal Long memberId)
    {
        EndChallengeResponse response = memberChallengeServcie.endChallenge(memberId);
        return ResponseEntity.ok(ApiResponse.success("참여 완료 챌린지 조회 성공",response));
    }

    @PutMapping("/endDate")
    public ResponseEntity<ApiResponse<EndDateChallengeResponse>> endDateChallenge(
            @AuthenticationPrincipal Long memberId)
    {
        EndDateChallengeResponse response = memberChallengeServcie.endDateChallenge(memberId);
        return ResponseEntity.ok(ApiResponse.success("기한지난 챌린지 자동 종료", response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProgressResponse>> progressChallenge(
            @AuthenticationPrincipal Long memberId,
            @RequestBody ProgressUpdateRequest request)
    {
        ProgressResponse response = memberChallengeServcie.progressChallenge(
                memberId,
                request.getMemberChallengeId(),
                request.getTimes()
        );

        return ResponseEntity.ok(ApiResponse.success("진행률 수정완료", response));
    }





}
