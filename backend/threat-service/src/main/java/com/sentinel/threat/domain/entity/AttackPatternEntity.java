package com.sentinel.threat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "attack_patterns")
public class AttackPatternEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String ruleCode;

    @Column(nullable = false, length = 50)
    private String patternType;

    @Column(nullable = false, length = 512)
    private String regexPattern;

    @Column(nullable = false)
    private boolean enabled;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    protected AttackPatternEntity() {
    }

    public AttackPatternEntity(String ruleCode, String patternType, String regexPattern, boolean enabled, String description) {
        this.id = UUID.randomUUID();
        this.ruleCode = ruleCode;
        this.patternType = patternType;
        this.regexPattern = regexPattern;
        this.enabled = enabled;
        this.description = description;
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

    public String getRuleCode() {
        return ruleCode;
    }

    public String getPatternType() {
        return patternType;
    }

    public String getRegexPattern() {
        return regexPattern;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
