package com.ucamp.greenmap.badge.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BadgeResponse {

    private String message;
    private List<BadgeInfo> data;

    @Data
    @Builder
    public static class BadgeInfo {
        private String name;
        private Long progress;
        private Long standard;
        private String description;
        private String image_url;
        private LocalDateTime created_at;
        private Boolean isAcquired;
        private Boolean isSelected;
    }
}
