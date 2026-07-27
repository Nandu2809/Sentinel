package com.sentinel.threat.mapper;

import com.sentinel.threat.domain.entity.IncidentEntity;
import com.sentinel.threat.domain.entity.ThreatEventEntity;
import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import com.sentinel.threat.dto.IncidentResponse;
import com.sentinel.threat.dto.ThreatEventResponse;
import com.sentinel.threat.dto.ThreatRuleDto;
import org.springframework.stereotype.Component;

@Component
public class ThreatMapper {

    public ThreatEventResponse toThreatEventResponse(ThreatEventEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ThreatEventResponse(
                entity.getId(),
                entity.getThreatCode(),
                entity.getCorrelationId(),
                entity.getRequestId(),
                entity.getUserId(),
                entity.getClientIp(),
                entity.getEndpoint(),
                entity.getHttpMethod(),
                entity.getThreatType(),
                entity.getMatchedRule() != null ? entity.getMatchedRule().getRuleCode() : null,
                entity.getSeverity(),
                entity.getRiskScore(),
                entity.getRecommendation(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public IncidentResponse toIncidentResponse(IncidentEntity entity) {
        if (entity == null) {
            return null;
        }
        return new IncidentResponse(
                entity.getId(),
                entity.getIncidentCode(),
                entity.getThreatType(),
                entity.getSeverity(),
                entity.getRiskScore(),
                entity.getAffectedEndpoint(),
                entity.getAffectedUser(),
                entity.getEvidenceJson(),
                entity.getStatus(),
                entity.getMitigationRecommendation(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public ThreatRuleDto toThreatRuleDto(ThreatRuleEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ThreatRuleDto(
                entity.getId(),
                entity.getRuleCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getThreatType(),
                entity.getSeverity(),
                entity.isEnabled(),
                entity.getThreshold(),
                entity.getRecommendation(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
