package com.sentinel.monitoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "apis")
public class ApiEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private String pathPattern;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private int rateLimitPerMin;

    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected ApiEntity() {
    }

    public ApiEntity(String name, String pathPattern, String method, boolean enabled, int rateLimitPerMin, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.pathPattern = pathPattern;
        this.method = method.toUpperCase();
        this.enabled = enabled;
        this.rateLimitPerMin = rateLimitPerMin;
        this.description = description;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void update(String name, String pathPattern, String method, boolean enabled, int rateLimitPerMin, String description) {
        this.name = name;
        this.pathPattern = pathPattern;
        this.method = method.toUpperCase();
        this.enabled = enabled;
        this.rateLimitPerMin = rateLimitPerMin;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public String getMethod() {
        return method;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getRateLimitPerMin() {
        return rateLimitPerMin;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
