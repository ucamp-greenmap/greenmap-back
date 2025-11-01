package com.ucamp.greenmap.challenge.service;

import com.ucamp.greenmap.challenge.dto.response.ChallengeAvailResponse;
import com.ucamp.greenmap.challenge.dto.response.MemberChallengeregis;

public interface MemberChallengeService {

    MemberChallengeregis registMemberChallenge(Long memberId, Long challengeId);

    ChallengeAvailResponse availChallenge(Long memberId);
}
