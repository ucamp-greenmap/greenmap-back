package com.ucamp.greenmap.point.repository;

import com.ucamp.greenmap.point.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findTop5ByMember_MemberIdOrderByCreatedAtDesc(Long memberId);

    List<PointHistory> findByMember_MemberIdOrderByCreatedAtDesc(Long memberId);

    List<PointHistory> findByMember_MemberIdAndCategory_CategoryIdInOrderByCreatedAtDesc(Long memberId, List<Long> categoryIds);

    Optional<PointHistory> findByLogId(Long logId);

    //---------------------------------마이페이지-------------------------------------------

    @Query(value = """
    SELECT COALESCE(SUM(point_amount),0)
    FROM point_history
    WHERE member_id = :memberId
    AND point_amount > 0
    AND MONTH(created_at) = MONTH(CURRENT_DATE())
    AND YEAR(created_at) = YEAR(CURRENT_DATE())
""", nativeQuery = true)
    Integer sumThisMonth(@Param("memberId") Long memberId);


    @Query(value = """
    SELECT COALESCE(SUM(point_amount),0)
    FROM point_history
    WHERE member_id = :memberId
    AND point_amount > 0
    AND MONTH(created_at) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))
    AND YEAR(created_at) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))
""", nativeQuery = true)
    Integer sumLastMonth(@Param("memberId") Long memberId);


}
