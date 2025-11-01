package com.ucamp.greenmap.challenge.dto.response;

import com.ucamp.greenmap.challenge.domain.Challenge;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ChallengeAvailResponse {
    private Long memberId;
    private Long memberChallengeId;
    private Long challengeId;
//    private String challengeName;
//    private String description;
//    private Long memberCount;
//    private Long deadline;
//    private Long success;
//    private Long pointAmount;
    private List<Challenge> availableChallenges;

}
