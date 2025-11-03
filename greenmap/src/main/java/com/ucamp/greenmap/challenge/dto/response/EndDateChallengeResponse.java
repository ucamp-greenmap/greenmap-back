package com.ucamp.greenmap.challenge.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class EndDateChallengeResponse {
    private Long memberId;
    private Long memberChallengeId;
    private Long challengeId;
    private String challengeName;
    private Long progress;
    private Long success;
    private Long deadline;
    private Boolean isActive;
}
