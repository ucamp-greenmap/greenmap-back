package com.ucamp.greenmap.verification.repository;

import com.ucamp.greenmap.verification.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
