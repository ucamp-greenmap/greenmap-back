package com.ucamp.greenmap.scheduler;

import com.ucamp.greenmap.challenge.domain.Challenge;
import com.ucamp.greenmap.challenge.repository.ChallengeRepository;
import com.ucamp.greenmap.challenge.repository.MemberChallengeRepository;
import com.ucamp.greenmap.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

    private final MemberRepository memberRepository;
    private final MemberChallengeRepository memberChallengeRepository;
    private final ChallengeRepository challengeRepository;

    // 매일 자정 실행
    @Scheduled(cron = "0 0 */6 * * *")
    public void autoCloseChallenges() {
        memberRepository.findAll().forEach(
                member -> memberChallengeRepository.updateExpiredChallenges()
        );
    }

    // 매일 자정 실행
    @Scheduled(cron = "0 0 */12 * * *")
    @Transactional
    public void autoCloseChallenge() {
//        List<Challenge> expiredChallenges = challengeRepository.findExpiredChallenges();
//
//        expiredChallenges.forEach(challenge -> challenge.setIsActive(false));
//
//        challengeRepository.saveAll(expiredChallenges);

        List<Challenge> challenges = challengeRepository.findAll();

        challenges.forEach(challenge -> {
            log.info("=======================================");
            log.info("12341" + String.valueOf(challenge.getUpdatedAt().isBefore(LocalDateTime.now())));
            log.info("12341" + String.valueOf(challenge.getIsActive()));

            if (challenge.getUpdatedAt().isBefore(LocalDateTime.now()) && challenge.getIsActive()) {
                challenge.setIsActive(false);
                log.info("jqkleqjfljflkajfdf");

            }
            challengeRepository.save(challenge);
        });


    }

}
