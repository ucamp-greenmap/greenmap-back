package com.ucamp.greenmap.verification.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
public class VerificationHistoryResponse {
    private List<VerificationHistoryItem> historyItems;

    @Data
    @Builder
    public static class VerificationHistoryItem {
        private String category;
        private String createdAt;
        private Long point;
    }
}
