package com.ucamp.greenmap.verification.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.verification.dto.request.BikeRequest;
import com.ucamp.greenmap.verification.dto.request.CarRequest;
import com.ucamp.greenmap.verification.dto.request.ShopRequest;
import com.ucamp.greenmap.verification.dto.response.MonthlyVerificationResponse;
import com.ucamp.greenmap.verification.dto.response.VerificationHistoryResponse;
import com.ucamp.greenmap.verification.dto.response.VerificationResponse;
import com.ucamp.greenmap.verification.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/verification")
@RequiredArgsConstructor
public class VerificationController {
    private final VerificationService verificationService;

    // 따릉이 인증
    @PostMapping("/bike")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyBike(@RequestHeader Long memberId, @RequestBody BikeRequest bikeRequest) {
        return ResponseEntity.ok(ApiResponse.success("인증에 성공했습니다", verificationService.verifyBike(memberId, bikeRequest)));
    }

    // 전기차/수소차 인증
    @PostMapping("/car")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyCar(@RequestHeader Long memberId, @RequestBody CarRequest carRequest) {
        return ResponseEntity.ok(ApiResponse.success("인증에 성공했습니다", verificationService.verifyCar(memberId, carRequest)));
    }

    // 친환경 가게 인증 (재활용센터, 제로웨이스트)
    @PostMapping("/shop")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyShop(@RequestHeader Long memberId, @RequestBody ShopRequest shopRequest) {
        return ResponseEntity.ok(ApiResponse.success("인증에 성공했습니다", verificationService.verifyShop(memberId, shopRequest)));
    }

    // 인증 내역 조회
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<VerificationHistoryResponse>> getVerificationHistory(@RequestHeader Long memberId) {
        return ResponseEntity.ok(ApiResponse.success("인증 내역 조회에 성공했습니다", verificationService.getVerificationHistory(memberId)));
    }

    // 이번달 인증 내역 조회
    @GetMapping("/month")
    public ResponseEntity<ApiResponse<MonthlyVerificationResponse>> getMonthlyVerification(@RequestHeader Long memberId) {
        return ResponseEntity.ok(ApiResponse.success("이번달 인증 내역 조회에 성공했습니다", verificationService.getMonthlyVerification(memberId)));
    }

}
