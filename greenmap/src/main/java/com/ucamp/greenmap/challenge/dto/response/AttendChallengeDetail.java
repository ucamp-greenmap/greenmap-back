package com.ucamp.greenmap.challenge.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
public class AttendChallengeDetail {
    private Long challengeId;
    private String challengeName;
    private String description;
    private Long pointAmount;
    private Long progress;
    private LocalDateTime createdAt;
    private Long memberId;
    private Long deadline;
    private Long memberCount;
    private Long success;
    private Boolean isActive;

}
