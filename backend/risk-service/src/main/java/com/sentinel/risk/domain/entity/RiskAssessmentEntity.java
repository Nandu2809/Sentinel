package com.sentinel.risk.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessmentEntity {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "threat_id")
    private UUID threatId;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Column(name = "risk_level", nullable = false, length = 30)
    private String riskLevel;

    @Column(name = "decision", nullable = false, length = 50)
    private String decision;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RiskAssessmentEntity() {
    }

    public RiskAssessmentEntity(UUID userId, UUID threatId, Integer riskScore, String riskLevel, String decision, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.threatId = threatId;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.decision = decision;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
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

    public UUID getUserId() {
        return userId;
    }

    public UUID getThreatId() {
        return threatId;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getDecision() {
        return decision;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
