package com.sentinel.threat.domain.entity;

import com.sentinel.threat.domain.model.IncidentStatus;
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
@Table(name = "incident_history")
public class IncidentEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String incidentCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private ThreatType threatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ThreatSeverity severity;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false, length = 512)
    private String affectedEndpoint;

    @Column(length = 80)
    private String affectedUser;

    @Column(columnDefinition = "TEXT")
    private String evidenceJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentStatus status;

    @Column(length = 512)
    private String mitigationRecommendation;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected IncidentEntity() {
    }

    public IncidentEntity(String incidentCode, ThreatType threatType, ThreatSeverity severity, double riskScore,
                          String affectedEndpoint, String affectedUser, String evidenceJson,
                          IncidentStatus status, String mitigationRecommendation) {
        this.id = UUID.randomUUID();
        this.incidentCode = incidentCode;
        this.threatType = threatType;
        this.severity = severity;
        this.riskScore = riskScore;
        this.affectedEndpoint = affectedEndpoint;
        this.affectedUser = affectedUser;
        this.evidenceJson = evidenceJson;
        this.status = status != null ? status : IncidentStatus.OPEN;
        this.mitigationRecommendation = mitigationRecommendation;
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

    public void updateStatus(IncidentStatus status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getIncidentCode() {
        return incidentCode;
    }

    public ThreatType getThreatType() {
        return threatType;
    }

    public ThreatSeverity getSeverity() {
        return severity;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public String getAffectedEndpoint() {
        return affectedEndpoint;
    }

    public String getAffectedUser() {
        return affectedUser;
    }

    public String getEvidenceJson() {
        return evidenceJson;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public String getMitigationRecommendation() {
        return mitigationRecommendation;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
