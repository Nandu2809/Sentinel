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
@Table(name = "notification_preferences")
public class NotificationPreferenceEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String userId;

    @Column(nullable = false)
    private boolean emailEnabled;

    @Column(nullable = false)
    private boolean websocketEnabled;

    @Column(length = 512)
    private String webhookUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AlertSeverity minSeverity;

    @Column(nullable = false)
    private Instant updatedAt;

    protected NotificationPreferenceEntity() {
    }

    public NotificationPreferenceEntity(String userId, boolean emailEnabled, boolean websocketEnabled, String webhookUrl, AlertSeverity minSeverity) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.emailEnabled = emailEnabled;
        this.websocketEnabled = websocketEnabled;
        this.webhookUrl = webhookUrl;
        this.minSeverity = minSeverity != null ? minSeverity : AlertSeverity.MEDIUM;
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

    public void updatePreferences(boolean emailEnabled, boolean websocketEnabled, String webhookUrl, AlertSeverity minSeverity) {
        this.emailEnabled = emailEnabled;
        this.websocketEnabled = websocketEnabled;
        this.webhookUrl = webhookUrl;
        if (minSeverity != null) {
            this.minSeverity = minSeverity;
        }
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public boolean isWebsocketEnabled() {
        return websocketEnabled;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public AlertSeverity getMinSeverity() {
        return minSeverity;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
