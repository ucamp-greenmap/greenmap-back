package com.ucamp.greenmap.challenge.repository;

import com.ucamp.greenmap.challenge.domain.Challenge;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;



public interface ChallengeRepository extends JpaRepository<Challenge,Long> {

    @Query("SELECT c FROM Challenge c " +
            "WHERE c.id NOT IN (SELECT mc.challenge.id FROM MemberChallenge mc WHERE mc.member.id = :memberId) AND isActive = true")
    List<Challenge> findAvailableChallengesByMemberId(Long memberId);


//    @Query("SELECT c FROM Challenge c WHERE c.updatedAt < CURRENT_TIMESTAMP AND c.isActive = true")
//    List<Challenge> findExpiredChallenges();

    List<Challenge> findAll();
}
