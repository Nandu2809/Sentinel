package com.sentinel.report.domain.entity;

import com.sentinel.report.domain.model.ReportFormat;
import com.sentinel.report.domain.model.ReportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "scheduled_reports")
public class ScheduledReportEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String jobName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportFormat format;

    @Column(nullable = false, length = 60)
    private String cronExpression;

    @Column(columnDefinition = "TEXT")
    private String recipientsJson;

    @Column(nullable = false)
    private boolean enabled;

    @Column
    private Instant lastRunAt;

    protected ScheduledReportEntity() {
    }

    public ScheduledReportEntity(String jobName, ReportType reportType, ReportFormat format,
                                 String cronExpression, String recipientsJson, boolean enabled) {
        this.id = UUID.randomUUID();
        this.jobName = jobName;
        this.reportType = reportType;
        this.format = format;
        this.cronExpression = cronExpression;
        this.recipientsJson = recipientsJson;
        this.enabled = enabled;
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public void markExecuted() {
        this.lastRunAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public String getRecipientsJson() {
        return recipientsJson;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getLastRunAt() {
        return lastRunAt;
    }
}
