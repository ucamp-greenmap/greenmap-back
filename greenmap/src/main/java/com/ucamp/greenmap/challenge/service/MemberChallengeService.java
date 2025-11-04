package com.ucamp.greenmap.challenge.service;

import com.ucamp.greenmap.challenge.dto.response.*;

public interface MemberChallengeService {

    MemberChallengeregis registMemberChallenge(Long memberId, Long challengeId);

    ChallengeAvailResponse availChallenge(Long memberId);

    AttendChallengeResponse attendChallenge(Long memberId);

    EndChallengeResponse endChallenge(Long memberId);

    EndDateChallengeResponse endDateChallenge(Long memberId);

    ProgressResponse progressChallenge(Long memberId, Long memberChallengeId, Long progress);



}
