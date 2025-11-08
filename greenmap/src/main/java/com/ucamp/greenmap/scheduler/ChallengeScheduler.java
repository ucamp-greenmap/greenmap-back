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

    @Scheduled(cron = "0 0 */6 * * *")
    public void autoCloseChallenges() {
        memberRepository.findAll().forEach(
                member -> memberChallengeRepository.updateExpiredChallenges()
        );
    }


    @Scheduled(cron = "* * */12 * * *")
    @Transactional
    public void autoCloseChallenge() {
//        List<Challenge> expiredChallenges = challengeRepository.findExpiredChallenges();
//
//        expiredChallenges.forEach(challenge -> challenge.setIsActive(false));
//
//        challengeRepository.saveAll(expiredChallenges);

        try {
            List<Challenge> challenges = challengeRepository.findAll();

            challenges.forEach(challenge -> {
                try {
                    // updatedAt이 null이면 비교x
                    if (challenge.getUpdatedAt() != null &&
                            challenge.getUpdatedAt().isBefore(LocalDateTime.now()) &&
                            challenge.getIsActive()){

                        challenge.setIsActive(false);
                        challengeRepository.save(challenge);
                        log.info(" Challenge 자동 비활성화: {} ", challenge.getChallengeId());
                    }
                } catch (Exception e) {
                    log.error(" Challenge {} 처리 중 오류 : {}", challenge.getChallengeId(), e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("autoCloseChallenge 스케줄러 실행 중 오류: {}", e.getMessage());
        }

    }

}
