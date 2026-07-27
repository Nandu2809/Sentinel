package com.sentinel.risk.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_risk")
public class UserRiskEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String userId;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false)
    private long authFailureCount;

    @Column(nullable = false, length = 30)
    private String privilegeLevel;

    @Column(nullable = false)
    private Instant lastAssessedAt;

    protected UserRiskEntity() {
    }

    public UserRiskEntity(String userId, double riskScore, long authFailureCount, String privilegeLevel) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.riskScore = riskScore;
        this.authFailureCount = authFailureCount;
        this.privilegeLevel = privilegeLevel != null ? privilegeLevel : "STANDARD";
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

    public void updateRisk(double riskScore, boolean isAuthFailure) {
        this.riskScore = riskScore;
        if (isAuthFailure) {
            this.authFailureCount++;
        }
        this.lastAssessedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public long getAuthFailureCount() {
        return authFailureCount;
    }

    public String getPrivilegeLevel() {
        return privilegeLevel;
    }

    public Instant getLastAssessedAt() {
        return lastAssessedAt;
    }
}
