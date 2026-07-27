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
@Table(name = "api_requests")
public class ApiRequestEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_id")
    private ApiEntity api;

    @Column(nullable = false, length = 80)
    private String requestId;

    @Column(nullable = false, length = 80)
    private String correlationId;

    @Column(nullable = false, length = 64)
    private String clientIp;

    @Column(length = 512)
    private String userAgent;

    @Column(nullable = false, length = 10)
    private String httpMethod;

    @Column(nullable = false, length = 512)
    private String uri;

    @Column(nullable = false)
    private int statusCode;

    @Column(nullable = false)
    private long latencyMs;

    @Column(nullable = false)
    private long requestSize;

    @Column(nullable = false)
    private long responseSize;

    @Column(length = 80)
    private String userId;

    @Column(nullable = false)
    private Instant timestamp;

    protected ApiRequestEntity() {
    }

    public ApiRequestEntity(ApiEntity api, String requestId, String correlationId, String clientIp,
                            String userAgent, String httpMethod, String uri, int statusCode,
                            long latencyMs, long requestSize, long responseSize, String userId, Instant timestamp) {
        this.id = UUID.randomUUID();
        this.api = api;
        this.requestId = requestId;
        this.correlationId = correlationId;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
        this.httpMethod = httpMethod.toUpperCase();
        this.uri = uri;
        this.statusCode = statusCode;
        this.latencyMs = latencyMs;
        this.requestSize = requestSize;
        this.responseSize = responseSize;
        this.userId = userId;
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

    public ApiEntity getApi() {
        return api;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public long getRequestSize() {
        return requestSize;
    }

    public long getResponseSize() {
        return responseSize;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
