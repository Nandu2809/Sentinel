package com.sentinel.auth.service;

import com.sentinel.auth.domain.model.AuditLogEntity;
import com.sentinel.auth.domain.model.AuditOutcome;
import com.sentinel.auth.event.SecurityEventPublisher;
import com.sentinel.auth.repository.AuditLogRepository;
import com.sentinel.common.events.SecurityEventEnvelope;
import com.sentinel.common.security.SecurityEventType;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditEventService {
    private static final Logger log = LoggerFactory.getLogger(AuditEventService.class);
    private final AuditLogRepository auditLogRepository;
    private final SecurityEventPublisher publisher;

    public AuditEventService(AuditLogRepository auditLogRepository, SecurityEventPublisher publisher) {
        this.auditLogRepository = auditLogRepository;
        this.publisher = publisher;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UUID actorUserId, SecurityEventType eventType, AuditOutcome outcome, RequestMetadata metadata,
                       String targetType, String targetId, String message) {
        auditLogRepository.save(new AuditLogEntity(
                actorUserId,
                eventType,
                outcome,
                metadata.ipAddress(),
                metadata.userAgent(),
                targetType,
                targetId,
                message,
                null));
        publisher.publish(new SecurityEventEnvelope(
                UUID.randomUUID(),
                Instant.now(),
                eventType,
                actorUserId,
                targetId,
                metadata.ipAddress(),
                metadata.userAgent(),
                outcome.name(),
                message
        ));
        log.info("security_event type={} outcome={} actor={} target_type={} target_id={} ip={}",
                eventType, outcome, actorUserId, targetType, targetId, metadata.ipAddress());
    }
}

