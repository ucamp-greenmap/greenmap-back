package com.ucamp.greenmap.challenge.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressUpdateRequest {
    private Long memberChallengeId;
    private Long times;//프론트에서 받아오는 값
    private Long progress;  // 새 진행률
    private Boolean isActive;  // 활성 상태
}
