package com.sentinel.report.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_metrics")
public class SecurityMetricsEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String metricName;

    @Column(nullable = false)
    private double metricValue;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Instant timestamp;

    protected SecurityMetricsEntity() {
    }

    public SecurityMetricsEntity(String metricName, double metricValue, String category) {
        this.id = UUID.randomUUID();
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.category = category;
        this.timestamp = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.timestamp == null) {
            this.timestamp = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getMetricName() {
        return metricName;
    }

    public double getMetricValue() {
        return metricValue;
    }

    public String getCategory() {
        return category;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
