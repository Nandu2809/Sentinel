package com.sentinel.report.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_statistics")
public class RiskStatisticsEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 50)
    private String entityType;

    @Column(nullable = false)
    private double averageScore;

    @Column(nullable = false)
    private double maxScore;

    @Column(nullable = false)
    private long highRiskCount;

    @Column(nullable = false)
    private Instant calculatedAt;

    protected RiskStatisticsEntity() {
    }

    public RiskStatisticsEntity(String entityType, double averageScore, double maxScore, long highRiskCount) {
        this.id = UUID.randomUUID();
        this.entityType = entityType;
        this.averageScore = averageScore;
        this.maxScore = maxScore;
        this.highRiskCount = highRiskCount;
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

    public String getEntityType() {
        return entityType;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public long getHighRiskCount() {
        return highRiskCount;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }
}
