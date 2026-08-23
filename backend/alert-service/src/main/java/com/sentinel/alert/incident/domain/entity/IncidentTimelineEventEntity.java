package com.sentinel.alert.incident.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "incident_timeline_events")
public class IncidentTimelineEventEntity {
    @Id
    private UUID id;

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(nullable = false, length = 80)
    private String eventType;

    @Column(nullable = false, length = 80)
    private String source;

    @Column(nullable = false, length = 512)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    @Column(nullable = false)
    private Instant timestamp;

    protected IncidentTimelineEventEntity() {
    }

    public IncidentTimelineEventEntity(UUID incidentId, String eventType, String source,
                                        String summary, String detailsJson, Instant timestamp) {
        this.id = UUID.randomUUID();
        this.incidentId = incidentId;
        this.eventType = eventType;
        this.source = source;
        this.summary = summary;
        this.detailsJson = detailsJson;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
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

    public String getEventType() {
        return eventType;
    }

    public String getSource() {
        return source;
    }

    public String getSummary() {
        return summary;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
