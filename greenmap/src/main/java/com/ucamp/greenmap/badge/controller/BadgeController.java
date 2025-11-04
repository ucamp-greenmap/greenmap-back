package com.ucamp.greenmap.badge.controller;

import com.ucamp.greenmap.badge.dto.response.BadgeResponse;
import com.ucamp.greenmap.badge.service.BadgeService;
import com.ucamp.greenmap.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/badge")
@RequiredArgsConstructor
public class BadgeController {
    private final BadgeService badgeService;

    // 뱃지 조회
    @GetMapping
    public ResponseEntity<ApiResponse<BadgeResponse>> getBadges(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(ApiResponse.success("인증에 성공했습니다", badgeService.searchBadges(memberId)));
    }

}
