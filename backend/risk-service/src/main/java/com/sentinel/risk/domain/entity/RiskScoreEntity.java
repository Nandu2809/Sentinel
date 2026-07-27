package com.sentinel.risk.domain.entity;

import com.sentinel.risk.domain.model.EntityType;
import com.sentinel.risk.domain.model.RiskClassification;
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
@Table(name = "risk_scores")
public class RiskScoreEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityType entityType;

    @Column(nullable = false, length = 120)
    private String entityId;

    @Column(nullable = false)
    private double riskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskClassification classification;

    @Column(columnDefinition = "TEXT")
    private String factorsJson;

    @Column(nullable = false)
    private Instant calculatedAt;

    protected RiskScoreEntity() {
    }

    public RiskScoreEntity(EntityType entityType, String entityId, double riskScore, String factorsJson) {
        this.id = UUID.randomUUID();
        this.entityType = entityType;
        this.entityId = entityId;
        this.riskScore = riskScore;
        this.classification = RiskClassification.fromScore(riskScore);
        this.factorsJson = factorsJson;
        this.calculatedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.calculatedAt == null) {
            this.calculatedAt = Instant.now();
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

    public double getRiskScore() {
        return riskScore;
    }

    public RiskClassification getClassification() {
        return classification;
    }

    public String getFactorsJson() {
        return factorsJson;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }
}
