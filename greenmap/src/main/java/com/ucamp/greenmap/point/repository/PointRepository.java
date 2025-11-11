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

    long countByMonthPointGreaterThanAndMember_IsActiveTrue(Long monthPoint);
}
