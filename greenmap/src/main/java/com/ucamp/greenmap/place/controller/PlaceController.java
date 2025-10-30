package com.ucamp.greenmap.place.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.place.dto.response.PlaceDetailResponse;
import com.ucamp.greenmap.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/{placeId}")
    public ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetails(
            @PathVariable("placeId") Long placeId,
            @RequestParam(name = "longitude", required = false) Double longitude,
            @RequestParam(name = "latitude",  required = false) Double latitude
    ) {
        // 유저가 있으면 넘기고 없으면 null로 넘기기
        PlaceDetailResponse response = placeService.getPlaceDetail(1L, placeId, longitude, latitude);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }
}
