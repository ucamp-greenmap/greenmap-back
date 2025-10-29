package com.ucamp.greenmap.point.repository;

import com.ucamp.greenmap.point.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findTop5ByMember_MemberIdOrderByCreatedAtDesc(Long memberId);
}
