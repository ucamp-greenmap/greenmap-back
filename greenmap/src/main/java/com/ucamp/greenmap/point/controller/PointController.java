package com.ucamp.greenmap.point.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.point.dto.request.UsePointRequest;
import com.ucamp.greenmap.point.dto.response.*;
import com.ucamp.greenmap.point.enums.Type;
import com.ucamp.greenmap.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping
    public ResponseEntity<ApiResponse<UsePointResponse>> usePoint(@AuthenticationPrincipal Long memberId, @RequestBody UsePointRequest request) {
        Long point = pointService.usePoint(request, memberId);
        UsePointResponse response = UsePointResponse.builder()
                .memberId(memberId)
                .point(point)
                .build();
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getPointInfo(@AuthenticationPrincipal Long memberId) {
        UserInfoResponse response = pointService.getPointInfo(memberId);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<ShopInfoDto>> getShopInfo(@AuthenticationPrincipal Long memberId) {
        ShopInfoDto response = pointService.getShopInfo(memberId);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/used")
    public ResponseEntity<ApiResponse<UsedPointLogResponse>> getUsedPointLogs(@AuthenticationPrincipal Long memberId) {
        UsedPointLogResponse response = pointService.getUsedPointLogs(memberId);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<RankingResponse>> getRanking(@AuthenticationPrincipal Long memberId){
        RankingResponse response = pointService.getRanking(memberId);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserPointInfo>> getUserPointInfo(@AuthenticationPrincipal Long memberId, @RequestParam Type type) {
        UserPointInfo response = pointService.getUserPointInfo(memberId, type);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/carbon")
    public ResponseEntity<ApiResponse<CarbonInfoResponse>> getCarbonInfo(@AuthenticationPrincipal Long memberId) {
        CarbonInfoResponse response = pointService.getCarbonInfo(memberId);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }
}
