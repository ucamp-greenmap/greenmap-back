package com.ucamp.greenmap.challenge.repository;

import com.ucamp.greenmap.challenge.domain.MemberChallenge;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberChallengeRepository extends JpaRepository<MemberChallenge,Long>{

    @Query("SELECT mc FROM MemberChallenge mc " +
            "JOIN mc.challenge c " +
            "WHERE mc.member.memberId = :memberId AND mc.isActive = true")
    List<MemberChallenge> findAttendChallengesByMemberId(Long memberId);

    @Query("SELECT mc FROM MemberChallenge mc WHERE mc.member.memberId = :memberId AND mc.isActive = false AND mc.progress = 100")
    List<MemberChallenge> findEndChallengesByMemberId(Long memberId);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = """
    UPDATE member_challenge mc
    JOIN challenge c ON mc.challenge_id = c.challenge_id
    SET mc.is_active = false
      AND DATE_ADD(mc.created_at, INTERVAL c.deadline DAY) < CURRENT_DATE()
""", nativeQuery = true)
    void updateExpiredChallenges();

    @Query(value = """
SELECT mc.*
    FROM member_challenge mc
    JOIN challenge c ON mc.challenge_id = c.challenge_id
      AND DATE_ADD(mc.created_at, INTERVAL c.deadline DAY) < CURRENT_DATE()
    ORDER BY DATE_ADD(mc.created_at, INTERVAL c.deadline DAY) DESC
""", nativeQuery = true)
    List<MemberChallenge> findExpiredChallenges();

    @Query("SELECT mc FROM MemberChallenge mc WHERE mc.member.memberId = :memberId AND mc.memberChallengeId = :memberChallengeId")
    Optional<MemberChallenge> findChallengeByMember(
            @Param("memberId") Long memberId,
            @Param("memberChallengeId") Long memberChallengeId
    );


    MemberChallenge findChallengeByMember_memberIdAndChallenge_challengeId(Long memberId, Long challengeId);
}
