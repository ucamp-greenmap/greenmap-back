package com.ucamp.greenmap.badge.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BadgeResponse {
    private String name;
    private Long wholePoint;
    private Long currentPoint;
    private String description;
    private String image_url;
    private LocalDateTime created_at;
    // 확장성
    private Long badge_count;
    private Long total_badge;
}
