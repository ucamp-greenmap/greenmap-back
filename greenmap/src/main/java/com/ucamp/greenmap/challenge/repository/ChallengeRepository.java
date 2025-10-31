package com.ucamp.greenmap.challenge.repository;

import com.ucamp.greenmap.challenge.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge,Long> {


}
