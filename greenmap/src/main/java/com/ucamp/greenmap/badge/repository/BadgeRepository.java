package com.ucamp.greenmap.badge.repository;

import com.ucamp.greenmap.badge.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
