package com.ucamp.greenmap.challenge.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChalDto {
    private Long memberId;
    private Long challengeId;
    private String challengeName;
    private String description;
    private Long deadline;
    private Long memberCount;
    private Long pointAmount;
    private Long success;
}
