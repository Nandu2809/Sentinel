package com.sentinel.auth.domain.model;

import com.sentinel.common.security.SecurityEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {
    @Id
    private UUID id;

    private UUID actorUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private SecurityEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuditOutcome outcome;

    @Column(length = 64)
    private String ipAddress;

    @Column(length = 512)
    private String userAgent;

    @Column(length = 80)
    private String targetType;

    @Column(length = 120)
    private String targetId;

    @Column(length = 512)
    private String message;

    @Column(name = "metadata_json")
    private String metadataJson;

    @Column(nullable = false)
    private Instant createdAt;

    protected AuditLogEntity() {
    }

    public AuditLogEntity(UUID actorUserId, SecurityEventType eventType, AuditOutcome outcome, String ipAddress,
                          String userAgent, String targetType, String targetId, String message, String metadataJson) {
        this.id = UUID.randomUUID();
        this.actorUserId = actorUserId;
        this.eventType = eventType;
        this.outcome = outcome;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.targetType = targetType;
        this.targetId = targetId;
        this.message = message;
        this.metadataJson = metadataJson;
        this.createdAt = Instant.now();
    }
}

