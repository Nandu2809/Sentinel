package com.sentinel.risk.mapper;

import com.sentinel.risk.domain.entity.OrganizationSecurityScoreEntity;
import com.sentinel.risk.domain.entity.RiskProfileEntity;
import com.sentinel.risk.domain.entity.RiskScoreEntity;
import com.sentinel.risk.dto.RiskProfileResponse;
import com.sentinel.risk.dto.RiskScoreResponse;
import com.sentinel.risk.dto.SecurityScoreResponse;
import org.springframework.stereotype.Component;

@Component
public class RiskMapper {

    public RiskScoreResponse toRiskScoreResponse(RiskScoreEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RiskScoreResponse(
                entity.getId(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getRiskScore(),
                entity.getClassification(),
                entity.getFactorsJson(),
                entity.getCalculatedAt()
        );
    }

    public RiskProfileResponse toRiskProfileResponse(RiskProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RiskProfileResponse(
                entity.getId(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getCurrentRiskScore(),
                entity.getMaxRiskScore(),
                entity.getRiskClassification(),
                entity.getThreatCount(),
                entity.getIncidentCount(),
                entity.getRiskTrend(),
                entity.getLastUpdatedAt()
        );
    }

    public SecurityScoreResponse toSecurityScoreResponse(OrganizationSecurityScoreEntity entity) {
        if (entity == null) {
            return new SecurityScoreResponse(100.0, 0.0, 0, 0, java.time.Instant.now());
        }
        return new SecurityScoreResponse(
                entity.getSecurityScore(),
                entity.getThreatHeatIndex(),
                entity.getActiveCriticalIncidents(),
                entity.getTotalEntitiesAtRisk(),
                entity.getCalculatedAt()
        );
    }
}
