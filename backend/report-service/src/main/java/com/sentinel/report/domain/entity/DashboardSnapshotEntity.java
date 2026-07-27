package com.sentinel.report.domain.entity;

import com.sentinel.report.domain.model.RiskLevel;
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
@Table(name = "dashboard_snapshots")
public class DashboardSnapshotEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private long totalApisMonitored;

    @Column(nullable = false)
    private long totalRequests;

    @Column(nullable = false)
    private long totalThreatsDetected;

    @Column(nullable = false)
    private long activeIncidents;

    @Column(nullable = false)
    private long criticalAlerts;

    @Column(nullable = false)
    private double currentSecurityScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RiskLevel riskLevel;

    @Column(nullable = false)
    private Instant capturedAt;

    protected DashboardSnapshotEntity() {
    }

    public DashboardSnapshotEntity(long totalApisMonitored, long totalRequests, long totalThreatsDetected,
                                   long activeIncidents, long criticalAlerts, double currentSecurityScore,
                                   RiskLevel riskLevel) {
        this.id = UUID.randomUUID();
        this.totalApisMonitored = totalApisMonitored;
        this.totalRequests = totalRequests;
        this.totalThreatsDetected = totalThreatsDetected;
        this.activeIncidents = activeIncidents;
        this.criticalAlerts = criticalAlerts;
        this.currentSecurityScore = currentSecurityScore;
        this.riskLevel = riskLevel != null ? riskLevel : RiskLevel.LOW;
        this.capturedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.capturedAt == null) {
            this.capturedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public long getTotalApisMonitored() {
        return totalApisMonitored;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getTotalThreatsDetected() {
        return totalThreatsDetected;
    }

    public long getActiveIncidents() {
        return activeIncidents;
    }

    public long getCriticalAlerts() {
        return criticalAlerts;
    }

    public double getCurrentSecurityScore() {
        return currentSecurityScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }
}
