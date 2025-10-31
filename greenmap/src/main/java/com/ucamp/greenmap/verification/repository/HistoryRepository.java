package com.ucamp.greenmap.verification.repository;

import com.ucamp.greenmap.verification.domain.History;
import com.ucamp.greenmap.verification.dto.response.HistoryCarbonDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("select h.historyId, coalesce(h.carbonSave, 0) from History h where h.historyId in :ids")
    List<HistoryCarbonDto> findCarbonByIds(@Param("ids") List<Long> ids);

    List<History> findByMember_MemberIdOrderByCreatedAtDesc(Long memberId);
}
