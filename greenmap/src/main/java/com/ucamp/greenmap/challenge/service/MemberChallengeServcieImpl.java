package com.ucamp.greenmap.challenge.service;

import com.ucamp.greenmap.challenge.domain.Challenge;
import com.ucamp.greenmap.challenge.domain.MemberChallenge;
import com.ucamp.greenmap.challenge.dto.response.*;
import com.ucamp.greenmap.challenge.repository.ChallengeRepository;
import com.ucamp.greenmap.challenge.repository.MemberChallengeRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberChallengeServcieImpl implements MemberChallengeService {

    private final MemberChallengeRepository memberChallengeRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;

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
    @Override
    public ChallengeAvailResponse availChallenge(Long memberId){

        // 1. 회원 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        // 2. 참여하지 않은 챌린지 조회
        List<Challenge> availableChallenges = challengeRepository.findAvailableChallengesByMemberId(memberId);

        return ChallengeAvailResponse.builder()
                .memberId(memberId)
                .availableChallenges(availableChallenges)
                .build();
    }

    @Override
    public AttendChallengeResponse attendChallenge(Long memberId) {
        // 1. 회원 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        // 2. 참여중인 챌린지 조회
        List<MemberChallenge> attendChallenges =
                memberChallengeRepository.findAttendChallengesByMemberId(memberId);

        // 3. DTO 리스트 생성
        List<ChallengeDetail> challengeDtoList = new ArrayList<>();

        for (MemberChallenge memberChallenge : attendChallenges) {
            Challenge challenge = memberChallenge.getChallenge();

            // DTO에 넣기
            ChallengeDetail attendChallengeDetail = ChallengeDetail.builder()
                    .challengeId(challenge.getChallengeId())
                    .challengeName(challenge.getChallengeName())
                    .description(challenge.getDescription())
                    .pointAmount(challenge.getPointAmount())
                    .progress(memberChallenge.getProgress())
                    .createdAt(memberChallenge.getCreatedAt())
                    .deadline(challenge.getDeadline())
                    .memberCount(challenge.getMemberCount())
                    .success(challenge.getSuccess())
                    .isActive(challenge.getIsActive())
                    .build();

            challengeDtoList.add(attendChallengeDetail);
        }

        // 4. 응답 DTO 반환
        return AttendChallengeResponse.builder()
                .memberId(memberId)
                .challenges(challengeDtoList)
                .build();
    }

    @Override
    public EndChallengeResponse endChallenge(Long memberId){
        // 1. 회원 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        // 2. 참여완료 챌린지 조회
        List<MemberChallenge> endChallenges =
                memberChallengeRepository.findEndChallengesByMemberId(memberId);

        // 3. DTO 리스트 생성
        List<ChallengeDetail> challengeDtoList = new ArrayList<>();

        for (MemberChallenge memberChallenge : endChallenges) {
            Challenge challenge = memberChallenge.getChallenge();

            // DTO에 넣기
            ChallengeDetail endChallengeDetail = ChallengeDetail.builder()
                    .challengeId(challenge.getChallengeId())
                    .challengeName(challenge.getChallengeName())
                    .description(challenge.getDescription())
                    .pointAmount(challenge.getPointAmount())
                    .progress(memberChallenge.getProgress())
                    .createdAt(memberChallenge.getCreatedAt())
                    .deadline(challenge.getDeadline())
                    .memberCount(challenge.getMemberCount())
                    .success(challenge.getSuccess())
                    .isActive(memberChallenge.getIsActive())
                    .build();

            challengeDtoList.add(endChallengeDetail);
        }
        // 4. 응답 DTO 반환
        return EndChallengeResponse.builder()
                .memberId(memberId)
                .challenges(challengeDtoList)
                .build();

    }

    @Override
    @Transactional
    public EndDateChallengeResponse endDateChallenge(Long memberId) {

        // 1. 회원 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        // 2. 마감 지난 챌린지 조회 (만료될 챌린지들)
        List<MemberChallenge> expiredBeforeUpdate =
                memberChallengeRepository.findExpiredChallenges(memberId);

        if (expiredBeforeUpdate.isEmpty()) {
            return EndDateChallengeResponse.builder()
                    .memberId(memberId)
                    .build(); // 만료된 챌린지 없으면 기본 반환
        }

        // 3. 만료 처리 실행 (isActive = false 업데이트)
        memberChallengeRepository.updateExpiredChallenges(memberId);

        // 4. 가장 최근에 만료된 챌린지 기준 (가장 늦게 끝난 챌린지)
        MemberChallenge expired = expiredBeforeUpdate.get(0);

        Challenge challenge = expired.getChallenge();

        return EndDateChallengeResponse.builder()
                .memberId(memberId)
                .memberChallengeId(expired.getMemberChallengeId())
                .challengeId(challenge.getChallengeId())
                .challengeName(challenge.getChallengeName())
                .progress(expired.getProgress())
                .success(challenge.getSuccess())
                .isActive(false)
                .build();
    }

    @Override
    public ProgressResponse progressChallenge(Long memberId, Long memberChallengeId, Long progress) {

        MemberChallenge memberChallenge = memberChallengeRepository
                .findChallengeByMember(memberId, memberChallengeId)
                .orElseThrow(() -> new RuntimeException("해당 챌린지가 존재하지 않거나 회원 소유가 아닙니다."));

        memberChallenge.updateProgress(progress);

        MemberChallenge updated = memberChallengeRepository.save(memberChallenge);

        return ProgressResponse.builder()
                .memberChallengeId(updated.getMemberChallengeId())
                .memberId(updated.getMember().getMemberId())
                .challengeId(updated.getChallenge().getChallengeId())
                .progress(updated.getProgress())
                .isActive(updated.getIsActive())
                .build();
    }








}
