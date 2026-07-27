package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.RiskProfileEntity;
import com.sentinel.risk.domain.model.EntityType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RiskProfileRepository extends JpaRepository<RiskProfileEntity, UUID> {
    Optional<RiskProfileEntity> findByEntityTypeAndEntityId(EntityType entityType, String entityId);
    Page<RiskProfileEntity> findByEntityTypeOrderByCurrentRiskScoreDesc(EntityType entityType, Pageable pageable);
    List<RiskProfileEntity> findTop10ByOrderByCurrentRiskScoreDesc();

    @Query("SELECT COUNT(p) FROM RiskProfileEntity p WHERE p.currentRiskScore >= 61.0")
    long countEntitiesAtHighOrCriticalRisk();
}
