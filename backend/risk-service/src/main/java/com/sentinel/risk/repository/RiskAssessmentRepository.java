package com.sentinel.risk.repository;

import com.sentinel.risk.domain.entity.RiskAssessmentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessmentEntity, UUID> {
    boolean existsByThreatId(UUID threatId);
    java.util.List<RiskAssessmentEntity> findByUserId(UUID userId);
}
