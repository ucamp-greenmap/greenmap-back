package com.ucamp.greenmap.challenge.repository;

import com.ucamp.greenmap.challenge.domain.MemberChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberChallengeRepository extends JpaRepository<MemberChallenge,Long>{

    @Query("SELECT mc FROM MemberChallenge mc " +
            "JOIN mc.challenge c " +
            "WHERE mc.member.memberId = :memberId")
    List<MemberChallenge> findAttendChallengesByMemberId(Long memberId);

    @Query("SELECT mc FROM MemberChallenge mc WHERE mc.member.memberId = :memberId AND mc.isActive = false")
    List<MemberChallenge> findEndChallengesByMemberId(Long memberId);



}
