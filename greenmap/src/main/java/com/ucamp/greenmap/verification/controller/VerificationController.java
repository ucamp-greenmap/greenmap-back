package com.ucamp.greenmap.verification.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.verification.dto.request.BikeRequest;
import com.ucamp.greenmap.verification.dto.request.CarRequest;
import com.ucamp.greenmap.verification.dto.request.ShopRequest;
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

}
