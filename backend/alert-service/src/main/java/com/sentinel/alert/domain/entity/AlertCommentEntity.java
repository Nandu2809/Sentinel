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
@Table(name = "alert_comments")
public class AlertCommentEntity {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private AlertEntity alert;

    @Column(nullable = false, length = 80)
    private String analyst;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private Instant timestamp;

    protected AlertCommentEntity() {
    }

    public AlertCommentEntity(AlertEntity alert, String analyst, String comment) {
        this.id = UUID.randomUUID();
        this.alert = alert;
        this.analyst = analyst;
        this.comment = comment;
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

    public String getAnalyst() {
        return analyst;
    }

    public String getComment() {
        return comment;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
