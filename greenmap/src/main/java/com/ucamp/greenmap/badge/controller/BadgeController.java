package com.ucamp.greenmap.badge.controller;

import com.ucamp.greenmap.badge.dto.response.BadgeResponse;
import com.ucamp.greenmap.badge.service.BadgeService;
import com.ucamp.greenmap.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/badge")
@RequiredArgsConstructor
public class BadgeController {
    private final BadgeService badgeService;

    @GetMapping
    public ResponseEntity<ApiResponse<BadgeResponse>> getBadges(@RequestHeader Long memberId) {
        return ResponseEntity.ok(ApiResponse.success(badgeService.searchBadges(memberId)));
    }

}
