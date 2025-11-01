package com.ucamp.greenmap.challenge.repository;

import com.ucamp.greenmap.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge,Long> {

    @Query("SELECT c FROM Challenge c " +
            "WHERE c.id NOT IN (SELECT mc.challenge.id FROM MemberChallenge mc WHERE mc.member.id = :memberId)")
    List<Challenge> findAvailableChallengesByMemberId(Long memberId);


}
