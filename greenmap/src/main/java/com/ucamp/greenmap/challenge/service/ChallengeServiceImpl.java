package com.ucamp.greenmap.challenge.service;


import com.ucamp.greenmap.challenge.domain.Challenge;
import com.ucamp.greenmap.challenge.dto.request.ChallengeRequest;
import com.ucamp.greenmap.challenge.dto.response.ChallengeResponse;
import com.ucamp.greenmap.challenge.repository.ChallengeRepository;
import com.ucamp.greenmap.member.dto.request.AdminRequest;
import com.ucamp.greenmap.member.dto.response.AdminResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;

    @Override
    public ChallengeResponse save(ChallengeRequest request) {

        Challenge challenge = Challenge.builder()
                .challengeName(request.getChallengeName())
                .deadline(request.getDeadline())
                .success(request.getSuccess())
                .description(request.getDescription())
                .memberCount(0L)
                .pointAmount(request.getPointAmount())
                .build();
        challenge.setUpdatedAt(request.getUpdatedAt());
        challengeRepository.save(challenge);

        return ChallengeResponse.builder()
                .challengeName(challenge.getChallengeName())
                .deadline(challenge.getDeadline())
                .success(challenge.getSuccess())
                .description(challenge.getDescription())
                .memberCount(challenge.getMemberCount())
                .pointAmount(challenge.getPointAmount())
                .updatedAt(challenge.getUpdatedAt())
                .build();

    }
}
