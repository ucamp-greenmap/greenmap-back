package com.ucamp.greenmap.challenge.dto.response;

import com.ucamp.greenmap.challenge.domain.Challenge;
import com.ucamp.greenmap.challenge.domain.MemberChallenge;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AttendChallengeResponse {
    private Long memberId;
    private List<AttendChallengeDetail> challenges;
}

