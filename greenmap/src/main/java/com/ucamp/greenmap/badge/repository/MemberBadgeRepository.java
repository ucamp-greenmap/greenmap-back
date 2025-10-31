package com.ucamp.greenmap.badge.repository;

import com.ucamp.greenmap.badge.domain.MemberBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {
    List<MemberBadge> findAllByMember_MemberId(Long memberId);
}
