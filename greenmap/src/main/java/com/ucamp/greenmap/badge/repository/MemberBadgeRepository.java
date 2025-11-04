package com.ucamp.greenmap.badge.repository;

import com.ucamp.greenmap.badge.domain.MemberBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {
    Optional<MemberBadge> findByMember_MemberId(Long memberId);
}
