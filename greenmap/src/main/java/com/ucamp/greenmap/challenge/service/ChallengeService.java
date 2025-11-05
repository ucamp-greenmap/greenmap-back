package com.ucamp.greenmap.challenge.service;


import com.ucamp.greenmap.challenge.dto.request.ChallengeRequest;
import com.ucamp.greenmap.challenge.dto.response.ChallengeResponse;
import com.ucamp.greenmap.member.dto.request.AdminRequest;
import com.ucamp.greenmap.member.dto.response.AdminResponse;

public interface ChallengeService {
    ChallengeResponse save(ChallengeRequest request);
}
