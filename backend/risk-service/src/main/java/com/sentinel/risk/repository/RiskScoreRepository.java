package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.RiskScoreEntity;
import com.sentinel.risk.domain.model.EntityType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RiskScoreRepository extends JpaRepository<RiskScoreEntity, UUID> {
    Page<RiskScoreEntity> findByEntityTypeAndEntityIdOrderByCalculatedAtDesc(EntityType entityType, String entityId, Pageable pageable);

    @Query("SELECT AVG(r.riskScore) FROM RiskScoreEntity r WHERE r.calculatedAt >= :since")
    Double getAverageRiskScoreSince(@Param("since") Instant since);

    @Query("SELECT r.classification AS classification, COUNT(r) AS cnt FROM RiskScoreEntity r WHERE r.calculatedAt >= :since GROUP BY r.classification")
    List<Object[]> findRiskDistributionSince(@Param("since") Instant since);
}
