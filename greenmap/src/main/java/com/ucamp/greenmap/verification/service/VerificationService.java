package com.ucamp.greenmap.verification.service;

import com.ucamp.greenmap.verification.dto.request.BikeRequest;
import com.ucamp.greenmap.verification.dto.request.CarRequest;
import com.ucamp.greenmap.verification.dto.request.ShopRequest;
import com.ucamp.greenmap.verification.dto.response.VerificationResponse;

public interface VerificationService {
    VerificationResponse verifyBike(Long memberId, BikeRequest bikeRequest);
    VerificationResponse verifyCar(Long memberId, CarRequest carRequest);
    VerificationResponse verifyShop(Long memberId, ShopRequest shopRequest);
}
