package com.sentinel.alert.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alert_assignments")
public class AlertAssignmentEntity {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertEntity alert;

    @Column(length = 80)
    private String previousAnalyst;

    @Column(nullable = false, length = 80)
    private String newAnalyst;

    @Column(nullable = false, length = 80)
    private String assignedBy;

    @Column(nullable = false)
    private Instant timestamp;

    protected AlertAssignmentEntity() {
    }

    public AlertAssignmentEntity(AlertEntity alert, String previousAnalyst, String newAnalyst, String assignedBy) {
        this.id = UUID.randomUUID();
        this.alert = alert;
        this.previousAnalyst = previousAnalyst;
        this.newAnalyst = newAnalyst;
        this.assignedBy = assignedBy;
        this.timestamp = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.timestamp == null) {
            this.timestamp = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public AlertEntity getAlert() {
        return alert;
    }

    public String getPreviousAnalyst() {
        return previousAnalyst;
    }

    public String getNewAnalyst() {
        return newAnalyst;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
