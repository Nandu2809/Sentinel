package com.sentinel.threat.domain.entity;

import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatStatus;
import com.sentinel.threat.domain.model.ThreatType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "threat_events")
public class ThreatEventEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String threatCode;

    @Column(nullable = false, length = 80)
    private String correlationId;

    @Column(nullable = false, length = 80)
    private String requestId;

    @Column(length = 80)
    private String userId;

    @Column(nullable = false, length = 64)
    private String clientIp;

    @Column(nullable = false, length = 512)
    private String endpoint;

    @Column(nullable = false, length = 10)
    private String httpMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private ThreatType threatType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_rule_id")
    private ThreatRuleEntity matchedRule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ThreatSeverity severity;

    @Column(nullable = false)
    private double riskScore;

    @Column(length = 512)
    private String recommendation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ThreatStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected ThreatEventEntity() {
    }

    public ThreatEventEntity(String threatCode, String correlationId, String requestId, String userId,
                             String clientIp, String endpoint, String httpMethod, ThreatType threatType,
                             ThreatRuleEntity matchedRule, ThreatSeverity severity, double riskScore,
                             String recommendation, ThreatStatus status, Instant createdAt) {
        this.id = UUID.randomUUID();
        this.threatCode = threatCode;
        this.correlationId = correlationId;
        this.requestId = requestId;
        this.userId = userId;
        this.clientIp = clientIp;
        this.endpoint = endpoint;
        this.httpMethod = httpMethod.toUpperCase();
        this.threatType = threatType;
        this.matchedRule = matchedRule;
        this.severity = severity;
        this.riskScore = riskScore;
        this.recommendation = recommendation;
        this.status = status != null ? status : ThreatStatus.NEW;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
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

    public String getThreatCode() {
        return threatCode;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getUserId() {
        return userId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public ThreatType getThreatType() {
        return threatType;
    }

    public ThreatRuleEntity getMatchedRule() {
        return matchedRule;
    }

    public ThreatSeverity getSeverity() {
        return severity;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public ThreatStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
