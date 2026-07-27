package com.sentinel.report.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alert_statistics")
public class AlertStatisticsEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private long totalAlerts;

    @Column(nullable = false)
    private double mttaSeconds;

    @Column(nullable = false)
    private double mttrSeconds;

    @Column(nullable = false)
    private double resolutionRate;

    @Column(nullable = false)
    private Instant calculatedAt;

    protected AlertStatisticsEntity() {
    }

    public AlertStatisticsEntity(long totalAlerts, double mttaSeconds, double mttrSeconds, double resolutionRate) {
        this.id = UUID.randomUUID();
        this.totalAlerts = totalAlerts;
        this.mttaSeconds = mttaSeconds;
        this.mttrSeconds = mttrSeconds;
        this.resolutionRate = resolutionRate;
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

    public long getTotalAlerts() {
        return totalAlerts;
    }

    public double getMttaSeconds() {
        return mttaSeconds;
    }

    public double getMttrSeconds() {
        return mttrSeconds;
    }

    public double getResolutionRate() {
        return resolutionRate;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }
}
