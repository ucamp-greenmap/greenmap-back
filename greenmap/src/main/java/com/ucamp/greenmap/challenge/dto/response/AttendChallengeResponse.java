package com.ucamp.greenmap.challenge.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AttendChallengeResponse {
    private Long memberId;
    private List<ChallengeDetail> challenges;
    private Long memberChallengeId;
}

