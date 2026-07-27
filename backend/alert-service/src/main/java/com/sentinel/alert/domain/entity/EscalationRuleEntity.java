package com.sentinel.alert.domain.entity;

import com.sentinel.alert.domain.model.AlertSeverity;
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
@Table(name = "escalation_rules")
public class EscalationRuleEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertSeverity severity;

    @Column(nullable = false)
    private int unacknowledgedTimeoutMinutes;

    @Column(nullable = false, length = 80)
    private String escalateToRole;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private Instant updatedAt;

    protected EscalationRuleEntity() {
    }

    public EscalationRuleEntity(String ruleName, AlertSeverity severity, int unacknowledgedTimeoutMinutes, String escalateToRole, boolean enabled) {
        this.id = UUID.randomUUID();
        this.ruleName = ruleName;
        this.severity = severity;
        this.unacknowledgedTimeoutMinutes = unacknowledgedTimeoutMinutes;
        this.escalateToRole = escalateToRole != null ? escalateToRole : "SOC_LEAD";
        this.enabled = enabled;
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

    public AlertSeverity getSeverity() {
        return severity;
    }

    public int getUnacknowledgedTimeoutMinutes() {
        return unacknowledgedTimeoutMinutes;
    }

    public String getEscalateToRole() {
        return escalateToRole;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
