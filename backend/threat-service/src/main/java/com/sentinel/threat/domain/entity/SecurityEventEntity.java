package com.sentinel.threat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_events")
public class SecurityEventEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, length = 80)
    private String sourceService;

    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    @Column(nullable = false)
    private Instant createdAt;

    protected SecurityEventEntity() {
    }

    public SecurityEventEntity(String eventType, String sourceService, String detailsJson) {
        this.id = UUID.randomUUID();
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.detailsJson = detailsJson;
        this.createdAt = Instant.now();
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

    public String getEventType() {
        return eventType;
    }

    public String getSourceService() {
        return sourceService;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
