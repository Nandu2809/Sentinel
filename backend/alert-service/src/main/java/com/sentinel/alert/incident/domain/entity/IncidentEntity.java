package com.sentinel.alert.incident.domain.entity;

import com.sentinel.alert.domain.model.AlertSeverity;
import com.sentinel.alert.incident.domain.model.IncidentStatus;
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
@Table(name = "incidents")
public class IncidentEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String incidentNumber;

    @Column(name = "alert_id")
    private UUID alertId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IncidentStatus status;

    @Column(length = 80)
    private String assignedAnalyst;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false)
    private double aiConfidence;

    @Column(length = 80)
    private String affectedUser;

    @Column(length = 64)
    private String affectedIp;

    @Column(length = 120)
    private String affectedDevice;

    @Column(columnDefinition = "TEXT")
    private String evidenceJson;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant resolvedAt;

    protected IncidentEntity() {
    }

    public IncidentEntity(String incidentNumber, UUID alertId, String title, String description,
                          AlertSeverity severity, IncidentStatus status, String assignedAnalyst,
                          double riskScore, double aiConfidence, String affectedUser,
                          String affectedIp, String affectedDevice, String evidenceJson) {
        this.id = UUID.randomUUID();
        this.incidentNumber = incidentNumber;
        this.alertId = alertId;
        this.title = title != null ? title : "Security Incident";
        this.description = description;
        this.severity = severity != null ? severity : AlertSeverity.HIGH;
        this.status = status != null ? status : IncidentStatus.OPEN;
        this.assignedAnalyst = assignedAnalyst;
        this.riskScore = riskScore;
        this.aiConfidence = aiConfidence;
        this.affectedUser = affectedUser;
        this.affectedIp = affectedIp;
        this.affectedDevice = affectedDevice;
        this.evidenceJson = evidenceJson;
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateStatus(IncidentStatus newStatus) {
        this.status = newStatus;
        if (newStatus == IncidentStatus.RESOLVED || newStatus == IncidentStatus.CLOSED) {
            if (this.resolvedAt == null) {
                this.resolvedAt = Instant.now();
            }
        }
        this.updatedAt = Instant.now();
    }

    public void assignAnalyst(String analyst) {
        this.assignedAnalyst = analyst;
        if (this.status == IncidentStatus.OPEN) {
            this.status = IncidentStatus.ACKNOWLEDGED;
        }
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getIncidentNumber() {
        return incidentNumber;
    }

    public UUID getAlertId() {
        return alertId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public String getAssignedAnalyst() {
        return assignedAnalyst;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public double getAiConfidence() {
        return aiConfidence;
    }

    public String getAffectedUser() {
        return affectedUser;
    }

    public String getAffectedIp() {
        return affectedIp;
    }

    public String getAffectedDevice() {
        return affectedDevice;
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

    public Instant getResolvedAt() {
        return resolvedAt;
    }
}
