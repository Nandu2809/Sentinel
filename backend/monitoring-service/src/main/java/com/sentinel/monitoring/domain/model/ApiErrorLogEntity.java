package com.sentinel.monitoring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_error_logs")
public class ApiErrorLogEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id")
    private ApiEntity api;

    @Column(nullable = false)
    private int statusCode;

    @Column(nullable = false, length = 512)
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    @Column(nullable = false)
    private Instant timestamp;

    protected ApiErrorLogEntity() {
    }

    public ApiErrorLogEntity(String requestId, ApiEntity api, int statusCode, String errorMessage, String stackTrace, Instant timestamp) {
        this.id = UUID.randomUUID();
        this.requestId = requestId;
        this.api = api;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
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

    public String getRequestId() {
        return requestId;
    }

    public ApiEntity getApi() {
        return api;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
