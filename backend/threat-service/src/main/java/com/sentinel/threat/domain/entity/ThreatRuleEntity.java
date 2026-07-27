package com.sentinel.threat.domain.entity;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "threat_rules")
public class ThreatRuleEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String ruleCode;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private ThreatType threatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ThreatSeverity severity;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private int threshold;

    @Column(length = 512)
    private String recommendation;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected ThreatRuleEntity() {
    }

    public ThreatRuleEntity(String ruleCode, String name, String description, ThreatType threatType,
                            ThreatSeverity severity, boolean enabled, int threshold, String recommendation) {
        this.id = UUID.randomUUID();
        this.ruleCode = ruleCode;
        this.name = name;
        this.description = description;
        this.threatType = threatType;
        this.severity = severity;
        this.enabled = enabled;
        this.threshold = threshold;
        this.recommendation = recommendation;
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

    public void update(String name, String description, ThreatSeverity severity, boolean enabled, int threshold, String recommendation) {
        this.name = name;
        this.description = description;
        this.severity = severity;
        this.enabled = enabled;
        this.threshold = threshold;
        this.recommendation = recommendation;
    }

    public UUID getId() {
        return id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ThreatType getThreatType() {
        return threatType;
    }

    public ThreatSeverity getSeverity() {
        return severity;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getThreshold() {
        return threshold;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
