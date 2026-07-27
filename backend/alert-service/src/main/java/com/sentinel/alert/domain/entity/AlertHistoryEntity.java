package com.sentinel.alert.domain.entity;

import com.sentinel.alert.domain.model.AlertStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alert_history")
public class AlertHistoryEntity {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertEntity alert;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertStatus newStatus;

    @Column(nullable = false, length = 80)
    private String changedBy;

    @Column(length = 255)
    private String reason;

    @Column(nullable = false)
    private Instant timestamp;

    protected AlertHistoryEntity() {
    }

    public AlertHistoryEntity(AlertEntity alert, AlertStatus previousStatus, AlertStatus newStatus, String changedBy, String reason) {
        this.id = UUID.randomUUID();
        this.alert = alert;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.reason = reason;
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

    public AlertStatus getPreviousStatus() {
        return previousStatus;
    }

    public AlertStatus getNewStatus() {
        return newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public String getReason() {
        return reason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
