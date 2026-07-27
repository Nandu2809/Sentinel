package com.sentinel.alert.mapper;

import com.sentinel.alert.domain.entity.AlertEntity;
import com.sentinel.alert.domain.entity.NotificationPreferenceEntity;
import com.sentinel.alert.dto.AlertResponse;
import com.sentinel.alert.dto.NotificationPreferenceResponse;
import org.springframework.stereotype.Component;

@Component
public class AlertMapper {

    public AlertResponse toAlertResponse(AlertEntity entity) {
        if (entity == null) {
            return null;
        }
        return new AlertResponse(
                entity.getId(),
                entity.getAlertCode(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getThreatType(),
                entity.getSeverity(),
                entity.getRiskScore(),
                entity.getSourceService(),
                entity.getAffectedApi(),
                entity.getAffectedUser(),
                entity.getAffectedIp(),
                entity.getCorrelationId(),
                entity.getEvidenceJson(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getAssignedAnalyst(),
                entity.getStatus(),
                entity.getResolutionNotes()
        );
    }

    public NotificationPreferenceResponse toPreferenceResponse(NotificationPreferenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new NotificationPreferenceResponse(
                entity.getId(),
                entity.getUserId(),
                entity.isEmailEnabled(),
                entity.isWebsocketEnabled(),
                entity.getWebhookUrl(),
                entity.getMinSeverity(),
                entity.getUpdatedAt()
        );
    }
}
