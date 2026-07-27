package com.sentinel.monitoring.service;

import com.sentinel.common.security.SecurityEventType;
import com.sentinel.monitoring.domain.model.AuditLogEntity;
import com.sentinel.monitoring.domain.model.AuditOutcome;
import com.sentinel.monitoring.repository.AuditLogRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditEventService {
    private final AuditLogRepository auditLogRepository;

    public AuditEventService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void record(UUID actorUserId, SecurityEventType eventType, AuditOutcome outcome,
                       RequestMetadata metadata, String targetType, String targetId, String message) {
        AuditLogEntity log = new AuditLogEntity(
                actorUserId,
                eventType.name(),
                outcome,
                metadata != null ? metadata.ipAddress() : null,
                metadata != null ? metadata.userAgent() : null,
                targetType,
                targetId,
                message,
                null
        );
        auditLogRepository.save(log);
    }
}
