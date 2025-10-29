package com.ucamp.greenmap.point.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.point.dto.request.UsePointRequest;
import com.ucamp.greenmap.point.dto.response.UsePointResponse;
import com.ucamp.greenmap.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
