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
@Table(name = "risk_history")
public class RiskHistoryEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityType entityType;

    @Column(nullable = false, length = 120)
    private String entityId;

    @Column(nullable = false)
    private double previousScore;

    @Column(nullable = false)
    private double newScore;

    @Column(length = 255)
    private String reason;

    @Column(nullable = false)
    private Instant timestamp;

    protected RiskHistoryEntity() {
    }

    public RiskHistoryEntity(EntityType entityType, String entityId, double previousScore, double newScore, String reason) {
        this.id = UUID.randomUUID();
        this.entityType = entityType;
        this.entityId = entityId;
        this.previousScore = previousScore;
        this.newScore = newScore;
        this.reason = reason;
        this.timestamp = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.timestamp == null) {
            this.timestamp = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public double getPreviousScore() {
        return previousScore;
    }

    public double getNewScore() {
        return newScore;
    }

    public String getReason() {
        return reason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
