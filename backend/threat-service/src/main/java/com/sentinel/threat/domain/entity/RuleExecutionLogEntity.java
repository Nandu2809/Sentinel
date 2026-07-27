package com.sentinel.threat.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rule_execution_logs")
public class RuleExecutionLogEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String ruleCode;

    @Column(nullable = false)
    private long executionTimeMs;

    @Column(nullable = false)
    private boolean matched;

    @Column(nullable = false)
    private Instant evaluatedAt;

    protected RuleExecutionLogEntity() {
    }

    public RuleExecutionLogEntity(String ruleCode, long executionTimeMs, boolean matched) {
        this.id = UUID.randomUUID();
        this.ruleCode = ruleCode;
        this.executionTimeMs = executionTimeMs;
        this.matched = matched;
        this.evaluatedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.evaluatedAt == null) {
            this.evaluatedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public boolean isMatched() {
        return matched;
    }

    public Instant getEvaluatedAt() {
        return evaluatedAt;
    }
}
