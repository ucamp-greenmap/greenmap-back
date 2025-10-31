package com.ucamp.greenmap.challenge.service;

import com.ucamp.greenmap.challenge.dto.response.MemberChallengeregis;

public interface MemberChallengeService {

    MemberChallengeregis registMemberChallenge(Long memberId, Long challengeId);
}
