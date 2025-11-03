package com.ucamp.greenmap.scheduler;

import com.ucamp.greenmap.challenge.repository.MemberChallengeRepository;
import com.ucamp.greenmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

    private final MemberRepository memberRepository;
    private final MemberChallengeRepository memberChallengeRepository;

    // 매일 자정 실행
    @Scheduled(cron = "0 0 */6 * * *")
    public void autoCloseChallenges() {
        memberRepository.findAll().forEach(
                member -> memberChallengeRepository.updateExpiredChallenges(member.getMemberId())
        );
    }

}
