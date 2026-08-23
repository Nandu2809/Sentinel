package com.sentinel.alert.incident.domain.entity;

import com.sentinel.alert.incident.domain.model.IncidentActionType;
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
@Table(name = "incident_actions")
public class IncidentActionEntity {
    @Id
    private UUID id;

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IncidentActionType actionType;

    @Column(nullable = false, length = 80)
    private String performedBy;

    @Column(length = 255)
    private String target;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private Instant timestamp;

    protected IncidentActionEntity() {
    }

    public IncidentActionEntity(UUID incidentId, IncidentActionType actionType,
                                String performedBy, String target, String details) {
        this.id = UUID.randomUUID();
        this.incidentId = incidentId;
        this.actionType = actionType;
        this.performedBy = performedBy;
        this.target = target;
        this.details = details;
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

    public UUID getIncidentId() {
        return incidentId;
    }

    public IncidentActionType getActionType() {
        return actionType;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public String getTarget() {
        return target;
    }

    public String getDetails() {
        return details;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
