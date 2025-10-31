package com.ucamp.greenmap.challenge.dto.request;

import com.ucamp.greenmap.challenge.domain.Challenge;
import com.ucamp.greenmap.member.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberChallengeDto {
    private Long memberChallengeId;
    private Long memberId;
    private Long challengeId;
    private Long progress;

}
