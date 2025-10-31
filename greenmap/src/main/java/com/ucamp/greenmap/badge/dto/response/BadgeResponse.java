package com.ucamp.greenmap.badge.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BadgeResponse {
    private Long badge_count;
    private Long total_badge;
    private Long success_rate;
    private List<BadgeItems>  badge_list;

    @Data
    @Builder
    public static class BadgeItems {
        private String name;
        private String description;
        private String image_url;
        private LocalDateTime created_at;
        private String level;
        private Boolean isFinish;
        private Long progress;
        private Long total_progress;
    }
}
