package com.ucamp.greenmap.point.repository;

import com.ucamp.greenmap.point.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
