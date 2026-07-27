package com.sentinel.report.repository;

import com.sentinel.report.domain.entity.RiskStatisticsEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskStatisticsRepository extends JpaRepository<RiskStatisticsEntity, UUID> {
    Optional<RiskStatisticsEntity> findTopByEntityTypeOrderByCalculatedAtDesc(String entityType);
}
