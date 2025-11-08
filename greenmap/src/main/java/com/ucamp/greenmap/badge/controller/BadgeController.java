package com.ucamp.greenmap.badge.controller;

import com.ucamp.greenmap.badge.dto.request.BadgeRequest;
import com.ucamp.greenmap.badge.dto.response.BadgeResponse;
import com.ucamp.greenmap.badge.service.BadgeService;
import com.ucamp.greenmap.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // 뱃지 등록
    @PostMapping
    public ResponseEntity<ApiResponse<String>> registerBadges(@AuthenticationPrincipal Long memberId,
                                                                     @RequestBody BadgeRequest request) {
        // 관리자 검증
        if (memberId != 1L) {
            throw new IllegalStateException("권한이 없습니다.");
        }
        return ResponseEntity.ok(ApiResponse.success("뱃지 등록에 성공했습니다", badgeService.addBadge(request)));
    }

    // 뱃지 선택
    @GetMapping("/select")
    public ResponseEntity<ApiResponse<String>> selectBadge(@AuthenticationPrincipal Long memberId,
                                                                  @RequestParam String badgeName) {
        return ResponseEntity.ok(ApiResponse.success("뱃지 선택에 성공했습니다", badgeService.selectBadges(memberId, badgeName)));
    }

}
