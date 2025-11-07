package com.ucamp.greenmap.scheduler;

import com.ucamp.greenmap.point.domain.Point;
import com.ucamp.greenmap.point.repository.PointRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PointScheduler {

    private final PointRepository pointRepository;

    // 매월 1일 자정에 실행
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void deleteMonthHistory() {
        pointRepository.findAll().forEach(Point::resetMonthPoint);
    }
}
