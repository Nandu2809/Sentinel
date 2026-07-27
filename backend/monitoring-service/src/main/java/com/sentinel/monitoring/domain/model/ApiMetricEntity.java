package com.sentinel.monitoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_metrics")
public class ApiMetricEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id", nullable = false)
    private ApiEntity api;

    @Column(nullable = false)
    private Instant timeBucket;

    @Column(nullable = false)
    private long requestCount;

    @Column(nullable = false)
    private long errorCount;

    @Column(nullable = false)
    private double avgLatencyMs;

    @Column(nullable = false)
    private double p99LatencyMs;

    @Column(nullable = false)
    private Instant createdAt;

    protected ApiMetricEntity() {
    }

    public ApiMetricEntity(ApiEntity api, Instant timeBucket, long requestCount, long errorCount, double avgLatencyMs, double p99LatencyMs) {
        this.id = UUID.randomUUID();
        this.api = api;
        this.timeBucket = timeBucket;
        this.requestCount = requestCount;
        this.errorCount = errorCount;
        this.avgLatencyMs = avgLatencyMs;
        this.p99LatencyMs = p99LatencyMs;
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

    public ApiEntity getApi() {
        return api;
    }

    public Instant getTimeBucket() {
        return timeBucket;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public double getAvgLatencyMs() {
        return avgLatencyMs;
    }

    public double getP99LatencyMs() {
        return p99LatencyMs;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
