package com.ucamp.greenmap.verification.repository;

import com.ucamp.greenmap.point.dto.request.MostActiveCategory;
import com.ucamp.greenmap.verification.domain.History;
import com.ucamp.greenmap.verification.dto.response.HistoryCarbonDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("select h.historyId, coalesce(h.carbonSave, 0) from History h where h.historyId in :ids")
    List<HistoryCarbonDto> findCarbonByIds(@Param("ids") List<Long> ids);

    List<History> findByMember_MemberIdOrderByCreatedAtDesc(Long memberId);
    @Query(value = """
    SELECT COUNT(*)
    FROM history
    WHERE member_id = :memberId
    AND MONTH(created_at) = MONTH(CURRENT_DATE())
    AND YEAR(created_at) = YEAR(CURRENT_DATE())
""", nativeQuery = true)
    Integer countThisMonth(@Param("memberId") Long memberId);


    @Query(value = """
    SELECT COUNT(*)
    FROM history
    WHERE member_id = :memberId
    AND MONTH(created_at) = MONTH(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))
    AND YEAR(created_at) = YEAR(DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH))
""", nativeQuery = true)
    Integer countLastMonth(@Param("memberId") Long memberId);


    @Query(value = """
    SELECT c.category_name AS categoryName,
           COUNT(*) AS cnt
    FROM history h
    JOIN category c ON h.category_id = c.category_id
    WHERE h.member_id = :memberId
      AND MONTH(h.created_at) = MONTH(CURRENT_DATE())
      AND YEAR(h.created_at) = YEAR(CURRENT_DATE())
    GROUP BY c.category_name
    ORDER BY cnt DESC
    LIMIT 1
""", nativeQuery = true)
    List<MostActiveCategory> mostActiveCategoryThisMonth(@Param("memberId") Long memberId);

}