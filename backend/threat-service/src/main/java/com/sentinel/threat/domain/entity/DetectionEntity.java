package com.sentinel.threat.domain.entity;

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
@Table(name = "detections")
public class DetectionEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "threat_event_id", nullable = false)
    private ThreatEventEntity threatEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private ThreatRuleEntity rule;

    @Column(nullable = false, length = 100)
    private String detectorName;

    @Column(length = 255)
    private String matchedPattern;

    @Column(columnDefinition = "TEXT")
    private String rawPayloadSample;

    @Column(nullable = false)
    private Instant detectedAt;

    protected DetectionEntity() {
    }

    public DetectionEntity(ThreatEventEntity threatEvent, ThreatRuleEntity rule, String detectorName,
                           String matchedPattern, String rawPayloadSample) {
        this.id = UUID.randomUUID();
        this.threatEvent = threatEvent;
        this.rule = rule;
        this.detectorName = detectorName;
        this.matchedPattern = matchedPattern;
        this.rawPayloadSample = rawPayloadSample;
        this.detectedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.detectedAt == null) {
            this.detectedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public ThreatEventEntity getThreatEvent() {
        return threatEvent;
    }

    public ThreatRuleEntity getRule() {
        return rule;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public String getMatchedPattern() {
        return matchedPattern;
    }

    public String getRawPayloadSample() {
        return rawPayloadSample;
    }

    public Instant getDetectedAt() {
        return detectedAt;
    }
}
