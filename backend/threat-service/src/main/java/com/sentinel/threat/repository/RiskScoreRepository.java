package com.sentinel.threat.repository;

import com.sentinel.threat.domain.entity.RiskScoreEntity;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RiskScoreRepository extends JpaRepository<RiskScoreEntity, UUID> {
    @Query("SELECT AVG(r.riskScore) FROM RiskScoreEntity r WHERE r.calculatedAt >= :since")
    Double getAverageRiskScoreSince(@Param("since") Instant since);
}
