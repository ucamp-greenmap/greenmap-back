package com.ucamp.greenmap.challenge.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressResponse {

    private Long memberChallengeId;
    private Long memberId;
    private Long challengeId;
    private Double progress;
    private Boolean isActive;
}
