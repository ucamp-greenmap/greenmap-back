package com.ucamp.greenmap.challenge.controller;

import com.ucamp.greenmap.challenge.dto.request.ChallengeRequest;
import com.ucamp.greenmap.challenge.dto.response.ChallengeResponse;
import com.ucamp.greenmap.challenge.service.ChallengeService;
import com.ucamp.greenmap.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chalregis")
@RequiredArgsConstructor
@Slf4j
public class ChallengeController {
    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChallengeResponse>> regisChallenge (@RequestBody ChallengeRequest request){
        ChallengeResponse response = challengeService.save(request);
        return ResponseEntity.ok(ApiResponse.success("챌린지 등록 성공",response));
    }

}
