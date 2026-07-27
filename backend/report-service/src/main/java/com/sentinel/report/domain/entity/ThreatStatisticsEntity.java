package com.sentinel.report.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "threat_statistics")
public class ThreatStatisticsEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String threatType;

    @Column(nullable = false)
    private long count;

    @Column(nullable = false, length = 30)
    private String severity;

    @Column(nullable = false)
    private Instant lastDetectedAt;

    protected ThreatStatisticsEntity() {
    }

    public ThreatStatisticsEntity(String threatType, long count, String severity) {
        this.id = UUID.randomUUID();
        this.threatType = threatType;
        this.count = count;
        this.severity = severity;
        this.lastDetectedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.lastDetectedAt == null) {
            this.lastDetectedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getThreatType() {
        return threatType;
    }

    public long getCount() {
        return count;
    }

    public String getSeverity() {
        return severity;
    }

    public Instant getLastDetectedAt() {
        return lastDetectedAt;
    }
}
