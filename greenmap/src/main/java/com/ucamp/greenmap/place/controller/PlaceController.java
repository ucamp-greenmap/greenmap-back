package com.ucamp.greenmap.place.controller;

import com.ucamp.greenmap.common.dto.ApiResponse;
import com.ucamp.greenmap.place.dto.response.PlaceDetailResponse;
import com.ucamp.greenmap.place.dto.response.PlaceListDto;
import com.ucamp.greenmap.place.dto.response.PlaceSearchDto;
import com.ucamp.greenmap.place.service.BikeService;
import com.ucamp.greenmap.place.service.KepcoEvIngestService;
import com.ucamp.greenmap.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final KepcoEvIngestService kepcoEvIngestService;
    private final BikeService bikeService;

    @GetMapping("/{placeId}")
    public ResponseEntity<ApiResponse<PlaceDetailResponse>> getPlaceDetails(
            @AuthenticationPrincipal Long memberId,
            @PathVariable("placeId") Long placeId,
            @RequestParam(name = "longitude", required = false) Double longitude,
            @RequestParam(name = "latitude",  required = false) Double latitude
    ) {
        // 유저가 있으면 넘기고 없으면 null로 넘기기
        PlaceDetailResponse response = placeService.getPlaceDetail(memberId, placeId, longitude, latitude);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/kepcoEV")
    public ResponseEntity<Map<String, Object>> sync(@RequestParam String addr) {
        int changed = kepcoEvIngestService.syncByAddress(addr);
        return ResponseEntity.ok(Map.of(
                "message", "OK",
                "addr", addr,
                "changed", changed
        ));
    }

    @GetMapping("/bike")
    public ResponseEntity<ApiResponse<String>> syncBikeStation() {
        return ResponseEntity.ok(ApiResponse.success(bikeService.saveBikeStations()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PlaceListDto>> getAllPlaces(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(name = "longitude") Double longitude,
            @RequestParam(name = "latitude") Double latitude
    ) {
        PlaceListDto response = placeService.getAllPlaces(memberId, longitude, latitude);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PlaceSearchDto>> searchPlaces(@RequestParam String search) {
        PlaceSearchDto response = placeService.searchPlaces(search);
        return ResponseEntity.ok(ApiResponse.success("성공", response));
    }
}
