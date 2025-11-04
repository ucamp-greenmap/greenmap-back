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
    SELECT COALESCE(SUM(p.point_amount), 0)
    FROM point_history p
    WHERE p.member_id = :memberId
      AND DATE_FORMAT(p.created_at, '%Y-%m') = DATE_FORMAT(CURRENT_DATE, '%Y-%m')
""", nativeQuery = true)
    Long sumThisMonthPoints(@Param("memberId") Long memberId);



    @Query(value = """
    SELECT COALESCE(SUM(p.point_amount), 0)
    FROM point_history p
    WHERE p.member_id = :memberId
      AND DATE_FORMAT(p.created_at, '%Y-%m') = DATE_FORMAT(CURRENT_DATE - INTERVAL 1 MONTH, '%Y-%m')
""", nativeQuery = true)
    Long sumLastMonthPoints(@Param("memberId") Long memberId);





}
