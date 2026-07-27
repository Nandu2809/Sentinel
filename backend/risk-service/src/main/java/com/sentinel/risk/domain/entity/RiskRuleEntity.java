package com.sentinel.risk.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_rules")
public class RiskRuleEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String ruleName;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private boolean enabled;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Instant updatedAt;

    protected RiskRuleEntity() {
    }

    public RiskRuleEntity(String ruleName, double weight, boolean enabled, String description) {
        this.id = UUID.randomUUID();
        this.ruleName = ruleName;
        this.weight = weight;
        this.enabled = enabled;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.updatedAt = now;
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDescription() {
        return description;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
