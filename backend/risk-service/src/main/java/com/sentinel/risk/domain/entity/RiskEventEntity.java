package com.sentinel.risk.domain.entity;

import com.sentinel.risk.domain.model.EntityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_events")
public class RiskEventEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityType entityType;

    @Column(nullable = false, length = 120)
    private String entityId;

    @Column(nullable = false)
    private double riskScore;

    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    @Column(nullable = false)
    private Instant createdAt;

    protected RiskEventEntity() {
    }

    public RiskEventEntity(String eventType, EntityType entityType, String entityId, double riskScore, String detailsJson) {
        this.id = UUID.randomUUID();
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.riskScore = riskScore;
        this.detailsJson = detailsJson;
        this.createdAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
