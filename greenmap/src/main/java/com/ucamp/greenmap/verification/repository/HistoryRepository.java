package com.ucamp.greenmap.verification.repository;

import com.ucamp.greenmap.verification.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("SELECT h.carbonSave FROM History h WHERE h.historyId = :historyId")
    Long getCarbonSaveByHistoryId(@Param("historyId") Long historyId);
}
