package com.ucamp.greenmap.challenge.service;

import com.ucamp.greenmap.badge.domain.Badge;
import com.ucamp.greenmap.badge.domain.MemberBadge;
import com.ucamp.greenmap.badge.repository.BadgeRepository;
import com.ucamp.greenmap.badge.repository.MemberBadgeRepository;
import com.ucamp.greenmap.challenge.domain.Challenge;
import com.ucamp.greenmap.challenge.domain.MemberChallenge;
import com.ucamp.greenmap.challenge.dto.response.*;
import com.ucamp.greenmap.challenge.repository.ChallengeRepository;
import com.ucamp.greenmap.challenge.repository.MemberChallengeRepository;
import com.ucamp.greenmap.common.domain.Category;
import com.ucamp.greenmap.common.domain.CategoryName;
import com.ucamp.greenmap.common.repository.CategoryRepository;
import com.ucamp.greenmap.member.domain.Member;
import com.ucamp.greenmap.member.repository.MemberRepository;
import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.domain.PointHistory;
import com.ucamp.greenmap.point.repository.PointHistoryRepository;
import com.ucamp.greenmap.point.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberChallengeServcieImpl implements MemberChallengeService {

    private final MemberChallengeRepository memberChallengeRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final MemberBadgeRepository memberBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public MemberChallengeregis registMemberChallenge(Long memberId, Long challengeId) {
        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        // 2. 챌린지 조회
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("CHALLENGE NOT FOUND"));

        MemberChallenge mc = memberChallengeRepository.findChallengeByMember_memberIdAndChallenge_challengeId(memberId, challengeId);
        if (mc != null) {
            return null;
        }
        // 3. 참여 엔티티 생성
        MemberChallenge memberChallenge = MemberChallenge.builder()
                .member(member)
                .challenge(challenge)
                .progress(0L)
                .build();
        memberChallenge.setCreatedAt();
        Challenge saveChallenge = Challenge.builder()
                .challengeId(challenge.getChallengeId())
                .challengeName(challenge.getChallengeName())
                .success(challenge.getSuccess())
                .deadline(challenge.getDeadline())
                .description(challenge.getDescription())
                .pointAmount(challenge.getPointAmount())
                .memberCount(challenge.getMemberCount()+1)
                        .build();
        saveChallenge.setCreatedAt(memberChallenge.getChallenge().getCreatedAt());
        saveChallenge.setUpdatedAt(memberChallenge.getChallenge().getUpdatedAt());

        // 4. 저장
        memberChallengeRepository.save(memberChallenge);
        challengeRepository.save(saveChallenge);

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
    public ChallengeAvailResponse availChallenge(Long memberId) {

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

            Double progress = (double) memberChallenge.getProgress();
            if (challenge.getDescription().startsWith("따릉이")) {
                progress = (double) memberChallenge.getProgress() / 1000;
            }

            // DTO에 넣기
            ChallengeDetail attendChallengeDetail = ChallengeDetail.builder()
                    .memberChallengeId(memberChallenge.getMemberChallengeId())
                    .challengeId(challenge.getChallengeId())
                    .challengeName(challenge.getChallengeName())
                    .description(challenge.getDescription())
                    .pointAmount(challenge.getPointAmount())
                    .progress(progress)
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
    public EndChallengeResponse endChallenge(Long memberId) {
        // 1. 회원 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND"));

        // 2. 참여완료 챌린지 조회
        List<MemberChallenge> endChallenges =
                memberChallengeRepository.findCompletedChallengesByMemberId(memberId);

        // 3. DTO 리스트 생성
        List<ChallengeDetail> challengeDtoList = new ArrayList<>();

        for (MemberChallenge memberChallenge : endChallenges) {
            Challenge challenge = memberChallenge.getChallenge();

            Double progress = (double) memberChallenge.getProgress();
            if (challenge.getDescription().startsWith("따릉이")) {
                progress = (double) memberChallenge.getProgress() / 1000;
            }

            // DTO에 넣기
            ChallengeDetail endChallengeDetail = ChallengeDetail.builder()
                    .challengeId(challenge.getChallengeId())
                    .challengeName(challenge.getChallengeName())
                    .description(challenge.getDescription())
                    .pointAmount(challenge.getPointAmount())
                    .progress(progress)
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
    public EndDateChallengeResponse endDateChallenge() {
        //끝난 챌린지 memberId받지 않고 구현

        // 2. 마감 지난 챌린지 조회 (만료될 챌린지들)
        List<MemberChallenge> expiredBeforeUpdate =
                memberChallengeRepository.findExpiredChallenges();

        if (expiredBeforeUpdate.isEmpty()) {
            return EndDateChallengeResponse.builder()
                    .build(); // 만료된 챌린지 없으면 기본 반환
        }

        // 3. 만료 처리 실행 (isActive = false 업데이트)
        memberChallengeRepository.updateExpiredChallenges();

        // 4. 가장 최근에 만료된 챌린지 기준 (가장 늦게 끝난 챌린지)
        MemberChallenge expired = expiredBeforeUpdate.get(0);

        Challenge challenge = expired.getChallenge();

        return EndDateChallengeResponse.builder()
                .memberId(expired.getMember().getMemberId())
                .memberChallengeId(expired.getMemberChallengeId())
                .challengeId(challenge.getChallengeId())
                .challengeName(challenge.getChallengeName())
                .progress(expired.getProgress())
                .success(challenge.getSuccess())
                .isActive(false)
                .build();
    }

    @Override
    @Transactional
    public ProgressResponse progressChallenge(Long memberId, Long memberChallengeId, Long times) {

        MemberChallenge memberChallenge = memberChallengeRepository
                .findChallengeByMember(memberId, memberChallengeId)
                .orElseThrow(() -> new RuntimeException("해당 챌린지가 존재하지 않거나 회원 소유가 아닙니다."));

        // 이미 완료된 챌린지면 그냥 현재 상태 반환
        if (Boolean.FALSE.equals(memberChallenge.getIsActive())) {
            return ProgressResponse.builder()
                    .memberChallengeId(memberChallengeId)
                    .memberId(memberId)
                    .challengeId(memberChallenge.getChallenge().getChallengeId())
                    .progress((double) memberChallenge.getProgress())
                    .isActive(false)
                    .build();
        }

        Challenge challenge = memberChallenge.getChallenge();
        Long goal = challenge.getSuccess();
        if (goal == null || goal <= 0) {
            throw new RuntimeException("챌린지 목표가 설정되지 않았습니다.");
        }

        // description으로 챌린지 종류 판별
        String description = challenge.getDescription();

        // progress 증가량
        Long increaseProgress = times;

        // 따릉이 챌린지면 km → m 단위 변환하여 저장
        boolean isBikeChallenge = description != null && description.startsWith("따릉이");

        // progress 누적
        Long newProgress = memberChallenge.getProgress() + increaseProgress;

        // 따릉이는 goal * 1000으로 비교 (2km → 2000m)
        Long maxProgress = isBikeChallenge ? goal * 1000 : goal;

        // 초과 방지
        newProgress = Math.min(newProgress, maxProgress);

        // progress 업데이트
        memberChallenge.updateProgress(newProgress);

        // 완료 조건
        if (newProgress >= maxProgress && Boolean.TRUE.equals(memberChallenge.getIsActive())) {

            Member member = memberChallenge.getMember();
            Long rewardPoint = challenge.getPointAmount();

            // 포인트 지급
            Point point = pointRepository.findByMember_MemberId(memberId)
                    .orElseThrow(() -> new RuntimeException("포인트 정보가 없습니다."));
            point.addPointChallenge(rewardPoint);

            // 포인트 히스토리 기록
            Category category = categoryRepository.findByCategoryName(CategoryName.CHALLENGE)
                    .orElseThrow(() -> new RuntimeException("해당 카테고리를 찾을 수 없습니다."));

            PointHistory pointHistory = PointHistory.builder()
                    .member(member)
                    .category(category)
                    .pointAmount(rewardPoint)
                    .description(challenge.getDescription())
                    .logId(memberChallengeId)
                    .build();
            pointHistory.setCreatedAt(LocalDateTime.now());
            pointHistoryRepository.save(pointHistory);

            // 뱃지 최신화
            // 멤버뱃지 조회
            List<MemberBadge> memberBadges = memberBadgeRepository.findByMember_MemberId(memberId);
            // 멤버뱃지 중 포인트 뱃지 진행도 업데이트
            for (MemberBadge memberBadge : memberBadges) {
                Badge badge = memberBadge.getBadge();
                Long requirement = badge.getRequirement();
                Long progress = memberBadge.getProgress();

                // 포인트 뱃지 진행도 업데이트
                if (memberBadge.getIsActive() && badge.getCategory().getCategoryName() == CategoryName.BADGE) {
                    progress += rewardPoint;
                    if (progress >= requirement) {
                        // 뱃지 획득
                        memberBadge.acquireBadge();
                    }
                    memberBadge.addBadgeProgress(progress);
                }
            }

            // 챌린지 완료 처리 (여기서만 isActive 바꿈)
            memberChallenge.completeChallenge();
        }

        MemberChallenge updated = memberChallengeRepository.save(memberChallenge);

        return ProgressResponse.builder()
                .memberChallengeId(updated.getMemberChallengeId())
                .memberId(updated.getMember().getMemberId())
                .challengeId(updated.getChallenge().getChallengeId())
                .progress((double) updated.getProgress() / 1000)
                .isActive(updated.getIsActive())
                .build();
    }

}
