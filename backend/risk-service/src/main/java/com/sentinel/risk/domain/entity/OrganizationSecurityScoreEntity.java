package com.sentinel.risk.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization_security_score")
public class OrganizationSecurityScoreEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private double securityScore;

    @Column(nullable = false)
    private double threatHeatIndex;

    @Column(nullable = false)
    private long activeCriticalIncidents;

    @Column(nullable = false)
    private long totalEntitiesAtRisk;

    @Column(nullable = false)
    private Instant calculatedAt;

    protected OrganizationSecurityScoreEntity() {
    }

    public OrganizationSecurityScoreEntity(double securityScore, double threatHeatIndex, long activeCriticalIncidents, long totalEntitiesAtRisk) {
        this.id = UUID.randomUUID();
        this.securityScore = securityScore;
        this.threatHeatIndex = threatHeatIndex;
        this.activeCriticalIncidents = activeCriticalIncidents;
        this.totalEntitiesAtRisk = totalEntitiesAtRisk;
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

    public double getSecurityScore() {
        return securityScore;
    }

    public double getThreatHeatIndex() {
        return threatHeatIndex;
    }

    public long getActiveCriticalIncidents() {
        return activeCriticalIncidents;
    }

    public long getTotalEntitiesAtRisk() {
        return totalEntitiesAtRisk;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }
}
