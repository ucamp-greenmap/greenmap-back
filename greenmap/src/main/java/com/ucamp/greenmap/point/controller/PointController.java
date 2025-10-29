package com.ucamp.greenmap.point.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.point.dto.request.UsePointRequest;
import com.ucamp.greenmap.point.dto.response.ShopInfoDto;
import com.ucamp.greenmap.point.dto.response.UsePointResponse;
import com.ucamp.greenmap.point.dto.response.UsedPointLogResponse;
import com.ucamp.greenmap.point.dto.response.UserInfoResponse;
import com.ucamp.greenmap.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping
    public ResponseEntity<ApiResponse<UsePointResponse>> usePoint(@RequestBody UsePointRequest request) {
        Long point = pointService.usePoint(request, 1L); // 임시로 1L로 설정 -> 추후 수정 예정
        UsePointResponse response = UsePointResponse.builder()
                .memberId(1L) // 임시로 1L로 설정 -> 추후 수정 예정
                .point(point)
                .build();
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getPointInfo() {
        Long memberId = 1L; // 임시로 1L로 설정 -> 추후 수정 예정
        UserInfoResponse response = pointService.getPointInfo(memberId);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<ShopInfoDto>> getShopInfo() {
        Long memberId = 1L; // 임시로 1L로 설정 -> 추후 수정 예정
        ShopInfoDto response = pointService.getShopInfo(memberId);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/used")
    public ResponseEntity<ApiResponse<UsedPointLogResponse>> getUsedPointLogs() {
        Long memberId = 1L; // 임시로 1L로 설정 -> 추후 수정 예정
        UsedPointLogResponse response = pointService.getUsedPointLogs(memberId);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

}
