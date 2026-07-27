package com.sentinel.alert.domain.entity;

import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.domain.model.AlertStatus;
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
@Table(name = "alerts")
public class AlertEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String alertCode;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 80)
    private String threatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertSeverity severity;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false, length = 80)
    private String sourceService;

    @Column(length = 512)
    private String affectedApi;

    @Column(length = 80)
    private String affectedUser;

    @Column(length = 64)
    private String affectedIp;

    @Column(length = 120)
    private String correlationId;

    @Column(columnDefinition = "TEXT")
    private String evidenceJson;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(length = 80)
    private String assignedAnalyst;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertStatus status;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    protected AlertEntity() {
    }

    public AlertEntity(String alertCode, String title, String description, String threatType,
                       AlertSeverity severity, double riskScore, String sourceService,
                       String affectedApi, String affectedUser, String affectedIp,
                       String correlationId, String evidenceJson) {
        this.id = UUID.randomUUID();
        this.alertCode = alertCode;
        this.title = title;
        this.description = description;
        this.threatType = threatType;
        this.severity = severity;
        this.riskScore = riskScore;
        this.sourceService = sourceService;
        this.affectedApi = affectedApi;
        this.affectedUser = affectedUser;
        this.affectedIp = affectedIp;
        this.correlationId = correlationId;
        this.evidenceJson = evidenceJson;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = AlertStatus.NEW;
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

    public void updateStatus(AlertStatus newStatus, String resolutionNotes) {
        this.status = newStatus;
        if (resolutionNotes != null) {
            this.resolutionNotes = resolutionNotes;
        }
        this.updatedAt = Instant.now();
    }

    public void assignAnalyst(String analyst) {
        this.assignedAnalyst = analyst;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getAlertCode() {
        return alertCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThreatType() {
        return threatType;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public String getSourceService() {
        return sourceService;
    }

    public String getAffectedApi() {
        return affectedApi;
    }

    public String getAffectedUser() {
        return affectedUser;
    }

    public String getAffectedIp() {
        return affectedIp;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getEvidenceJson() {
        return evidenceJson;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getAssignedAnalyst() {
        return assignedAnalyst;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }
}
