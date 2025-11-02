package com.ucamp.greenmap.challenge.dto.response;

import com.ucamp.greenmap.challenge.domain.Challenge;
import com.ucamp.greenmap.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MemberChallengeregis {
    private Long memberId;
    private Long memberChallengeId;
    private Long challengeId;
    private String challengeName;
    private Long progress;
}
