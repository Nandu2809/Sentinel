package com.sentinel.monitoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_health")
public class ApiHealthEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String serviceName;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false)
    private long responseTimeMs;

    @Column(nullable = false)
    private Instant lastCheckedAt;

    protected ApiHealthEntity() {
    }

    public ApiHealthEntity(String serviceName, String status, long responseTimeMs) {
        this.id = UUID.randomUUID();
        this.serviceName = serviceName;
        this.status = status;
        this.responseTimeMs = responseTimeMs;
        this.lastCheckedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.lastCheckedAt == null) {
            this.lastCheckedAt = Instant.now();
        }
    }

    public void updateHealth(String status, long responseTimeMs) {
        this.status = status;
        this.responseTimeMs = responseTimeMs;
        this.lastCheckedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getStatus() {
        return status;
    }

    public long getResponseTimeMs() {
        return responseTimeMs;
    }

    public Instant getLastCheckedAt() {
        return lastCheckedAt;
    }
}
