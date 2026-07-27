package com.sentinel.risk.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "endpoint_risk")
public class EndpointRiskEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 512)
    private String endpoint;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false)
    private boolean isCriticalAsset;

    @Column(nullable = false)
    private long requestCount;

    @Column(nullable = false)
    private long exploitAttemptCount;

    @Column(nullable = false)
    private Instant lastAssessedAt;

    protected EndpointRiskEntity() {
    }

    public EndpointRiskEntity(String endpoint, double riskScore, boolean isCriticalAsset, long requestCount, long exploitAttemptCount) {
        this.id = UUID.randomUUID();
        this.endpoint = endpoint;
        this.riskScore = riskScore;
        this.isCriticalAsset = isCriticalAsset;
        this.requestCount = requestCount;
        this.exploitAttemptCount = exploitAttemptCount;
        this.lastAssessedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.lastAssessedAt == null) {
            this.lastAssessedAt = Instant.now();
        }
    }

    public void updateRisk(double riskScore, boolean isExploit) {
        this.riskScore = riskScore;
        this.requestCount++;
        if (isExploit) {
            this.exploitAttemptCount++;
        }
        this.lastAssessedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public boolean isCriticalAsset() {
        return isCriticalAsset;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public long getExploitAttemptCount() {
        return exploitAttemptCount;
    }

    public Instant getLastAssessedAt() {
        return lastAssessedAt;
    }
}
