package com.ucamp.greenmap.challenge.service;

import com.ucamp.greenmap.challenge.domain.Challenge;
import com.ucamp.greenmap.challenge.domain.MemberChallenge;
import com.ucamp.greenmap.challenge.dto.response.MemberChallengeregis;
import com.ucamp.greenmap.challenge.repository.ChallengeRepository;
import com.ucamp.greenmap.challenge.repository.MemberChallengeRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberChallengeServcieImpl implements MemberChallengeService {

    private final MemberChallengeRepository memberChallengeRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository; // 필요

    @Override
    public MemberChallengeregis registMemberChallenge(Long memberId, Long challengeId){

        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        // 2. 챌린지 조회
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("CHALLENGE NOT FOUND"));

        // 3. 참여 엔티티 생성
        MemberChallenge memberChallenge = MemberChallenge.builder()
                .member(member)
                .challenge(challenge)
                .progress(0L)
                .build();

        // 4. 저장
        memberChallengeRepository.save(memberChallenge);

        // 5. DTO로 변환하여 리턴
        return MemberChallengeregis.builder()
                .memberChallengeId(memberChallenge.getMemberChallengeId())
                .memberId(memberChallenge.getMember().getMemberId())
                .challengeId(memberChallenge.getChallenge().getChallengeId())
                .challengeName(challenge.getChallengeName())
                .progress(0L)
                .build();
    }
}
