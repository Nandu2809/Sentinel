package com.sentinel.risk.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ip_risk")
public class IpRiskEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String clientIp;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false, length = 30)
    private String reputationStatus;

    @Column(length = 10)
    private String countryCode;

    @Column(nullable = false)
    private boolean isKnownBot;

    @Column(nullable = false)
    private Instant lastAssessedAt;

    protected IpRiskEntity() {
    }

    public IpRiskEntity(String clientIp, double riskScore, String reputationStatus, String countryCode, boolean isKnownBot) {
        this.id = UUID.randomUUID();
        this.clientIp = clientIp;
        this.riskScore = riskScore;
        this.reputationStatus = reputationStatus != null ? reputationStatus : "NEUTRAL";
        this.countryCode = countryCode != null ? countryCode : "UNKNOWN";
        this.isKnownBot = isKnownBot;
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

    public void updateRisk(double riskScore, boolean isBot) {
        this.riskScore = riskScore;
        if (isBot) {
            this.isKnownBot = true;
        }
        if (riskScore >= 75.0) {
            this.reputationStatus = "SUSPICIOUS";
        } else if (riskScore >= 90.0) {
            this.reputationStatus = "MALICIOUS";
        }
        this.lastAssessedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getClientIp() {
        return clientIp;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public String getReputationStatus() {
        return reputationStatus;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public boolean isKnownBot() {
        return isKnownBot;
    }

    public Instant getLastAssessedAt() {
        return lastAssessedAt;
    }
}
