package com.sentinel.alert.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alert_events")
public class AlertEventEntity {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertEntity alert;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, length = 80)
    private String sourceService;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    @Column(nullable = false)
    private Instant receivedAt;

    protected AlertEventEntity() {
    }

    public AlertEventEntity(AlertEntity alert, String eventType, String sourceService, String payloadJson) {
        this.id = UUID.randomUUID();
        this.alert = alert;
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.payloadJson = payloadJson;
        this.receivedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.receivedAt == null) {
            this.receivedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public AlertEntity getAlert() {
        return alert;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSourceService() {
        return sourceService;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
