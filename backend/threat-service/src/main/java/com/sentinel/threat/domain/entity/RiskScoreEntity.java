package com.sentinel.threat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(nullable = false, length = 64)
    private String clientIp;

    @Column(length = 80)
    private String userId;

    @Column(nullable = false, length = 512)
    private String endpoint;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false)
    private Instant calculatedAt;

    protected RiskScoreEntity() {
    }

    public RiskScoreEntity(String clientIp, String userId, String endpoint, double riskScore) {
        this.id = UUID.randomUUID();
        this.clientIp = clientIp;
        this.userId = userId;
        this.endpoint = endpoint;
        this.riskScore = riskScore;
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

    public String getClientIp() {
        return clientIp;
    }

    public String getUserId() {
        return userId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }
}
