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
@CrossOrigin(origins = "http://localhost:5173")
public class VerificationController {
    private final VerificationService verificationService;

    @PostMapping("/bike")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyBike(@RequestHeader Long memberId, @RequestBody BikeRequest bikeRequest) {

        return ResponseEntity.ok(ApiResponse.success("인증에 성공했습니다", verificationService.verifyBike(memberId, bikeRequest)));
    }

    @PostMapping("/car")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyCar(@RequestHeader Long memberId, @RequestBody CarRequest carRequest) {

        return ResponseEntity.ok(ApiResponse.success("인증에 성공했습니다", verificationService.verifyCar(memberId, carRequest)));
    }

    @PostMapping("/shop")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyShop(@RequestHeader Long memberId, @RequestBody ShopRequest shopRequest) {

        return ResponseEntity.ok(ApiResponse.success("인증에 성공했습니다", verificationService.verifyShop(memberId, shopRequest)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<VerificationHistoryResponse>> getVerificationHistory(@RequestHeader Long memberId) {
        return ResponseEntity.ok(ApiResponse.success("인증 내역 조회에 성공했습니다", verificationService.getVerificationHistory(memberId)));
    }

    @GetMapping("/month")
    public ResponseEntity<ApiResponse<MonthlyVerificationResponse>> getMonthlyVerification(@RequestHeader Long memberId) {
        return ResponseEntity.ok(ApiResponse.success("이번달 인증 내역 조회에 성공했습니다", verificationService.getMonthlyVerification(memberId)));
    }

}
