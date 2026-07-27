package com.sentinel.alert.domain.entity;

import com.sentinel.alert.domain.model.NotificationChannel;
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
@Table(name = "notification_logs")
public class NotificationLogEntity {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertEntity alert;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationChannel channel;

    @Column(nullable = false, length = 255)
    private String recipient;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(length = 512)
    private String errorMessage;

    @Column(nullable = false)
    private Instant sentAt;

    protected NotificationLogEntity() {
    }

    public NotificationLogEntity(AlertEntity alert, NotificationChannel channel, String recipient, String status, String errorMessage) {
        this.id = UUID.randomUUID();
        this.alert = alert;
        this.channel = channel;
        this.recipient = recipient;
        this.status = status;
        this.errorMessage = errorMessage;
        this.sentAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.sentAt == null) {
            this.sentAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public AlertEntity getAlert() {
        return alert;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}
