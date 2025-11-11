package com.ucamp.greenmap.point.repository;

import com.ucamp.greenmap.point.domain.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Query("select p from Point p join fetch p.member m where m.memberId = :memberId")
    Optional<Point> findByMemberIdForUpdate(@Param("memberId") Long memberId);

    Optional<Point> findByMember_MemberId(Long memberId);

    List<Point> findTop10ByMember_IsActiveTrueOrderByMonthPointDesc();

    @Query(value = """
        WITH ranked AS (
            SELECT
                m.member_id,
                COALESCE(p.month_point, 0) AS month_point,
                RANK() OVER (ORDER BY COALESCE(p.month_point, 0) DESC, m.member_id ASC) AS ranking
            FROM member m
            LEFT JOIN point p ON p.member_id = m.member_id
            WHERE m.is_active = TRUE
        )
        SELECT ranking
        FROM ranked
        WHERE member_id = :memberId
        """, nativeQuery = true)
    Optional<Long> findMemberRank(@Param("memberId") Long memberId);
}
