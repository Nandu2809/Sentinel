package com.sentinel.risk.domain.entity;

import com.sentinel.risk.domain.model.EntityType;
import com.sentinel.risk.domain.model.RiskClassification;
import com.sentinel.risk.domain.model.RiskTrend;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_profiles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"entityType", "entityId"})
})
public class RiskProfileEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EntityType entityType;

    @Column(nullable = false, length = 120)
    private String entityId;

    @Column(nullable = false)
    private double currentRiskScore;

    @Column(nullable = false)
    private double maxRiskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskClassification riskClassification;

    @Column(nullable = false)
    private long threatCount;

    @Column(nullable = false)
    private long incidentCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskTrend riskTrend;

    @Column(nullable = false)
    private Instant lastUpdatedAt;

    protected RiskProfileEntity() {
    }

    public RiskProfileEntity(EntityType entityType, String entityId, double currentRiskScore) {
        this.id = UUID.randomUUID();
        this.entityType = entityType;
        this.entityId = entityId;
        this.currentRiskScore = currentRiskScore;
        this.maxRiskScore = currentRiskScore;
        this.riskClassification = RiskClassification.fromScore(currentRiskScore);
        this.threatCount = 0;
        this.incidentCount = 0;
        this.riskTrend = RiskTrend.STABLE;
        this.lastUpdatedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.lastUpdatedAt = now;
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @PreUpdate
    void onUpdate() {
        this.lastUpdatedAt = Instant.now();
    }

    public void updateScore(double newScore, boolean isThreat, boolean isIncident) {
        if (newScore > this.currentRiskScore) {
            this.riskTrend = RiskTrend.DETERIORATING;
        } else if (newScore < this.currentRiskScore) {
            this.riskTrend = RiskTrend.IMPROVING;
        } else {
            this.riskTrend = RiskTrend.STABLE;
        }

        this.currentRiskScore = newScore;
        if (newScore > this.maxRiskScore) {
            this.maxRiskScore = newScore;
        }
        this.riskClassification = RiskClassification.fromScore(newScore);

        if (isThreat) {
            this.threatCount++;
        }
        if (isIncident) {
            this.incidentCount++;
        }
        this.lastUpdatedAt = Instant.now();
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

    public double getCurrentRiskScore() {
        return currentRiskScore;
    }

    public double getMaxRiskScore() {
        return maxRiskScore;
    }

    public RiskClassification getRiskClassification() {
        return riskClassification;
    }

    public long getThreatCount() {
        return threatCount;
    }

    public long getIncidentCount() {
        return incidentCount;
    }

    public RiskTrend getRiskTrend() {
        return riskTrend;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }
}
