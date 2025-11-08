package com.ucamp.greenmap.badge.repository;

import com.ucamp.greenmap.badge.domain.MemberBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {
    List<MemberBadge> findByMember_MemberId(Long memberId);

    @Query("""
           SELECT mb
           FROM MemberBadge mb
           JOIN FETCH mb.badge b
           JOIN FETCH b.image img
           WHERE mb.member.memberId IN :memberIds
             AND mb.isSelected = true
           """)
    List<MemberBadge> findSelectedBadges(@Param("memberIds") List<Long> memberIds);
}
