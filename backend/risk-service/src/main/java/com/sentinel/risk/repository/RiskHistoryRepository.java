package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.RiskHistoryEntity;
import com.sentinel.risk.domain.model.EntityType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskHistoryRepository extends JpaRepository<RiskHistoryEntity, UUID> {
    Page<RiskHistoryEntity> findByEntityTypeAndEntityIdOrderByTimestampDesc(EntityType entityType, String entityId, Pageable pageable);
    List<RiskHistoryEntity> findTop50ByOrderByTimestampDesc();
}
