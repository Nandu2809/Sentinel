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
@Table(name = "report_history")
public class ReportHistoryEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String reportCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportFormat format;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String contentJson;

    @Column(nullable = false, length = 80)
    private String createdBy;

    @Column(nullable = false)
    private Instant generatedAt;

    protected ReportHistoryEntity() {
    }

    public ReportHistoryEntity(String reportCode, ReportType reportType, ReportFormat format,
                               String title, String summary, String contentJson, String createdBy) {
        this.id = UUID.randomUUID();
        this.reportCode = reportCode;
        this.reportType = reportType;
        this.format = format;
        this.title = title;
        this.summary = summary;
        this.contentJson = contentJson;
        this.createdBy = createdBy;
        this.generatedAt = Instant.now();
    }

    @PrePersist
    void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.generatedAt == null) {
            this.generatedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getReportCode() {
        return reportCode;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getContentJson() {
        return contentJson;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }
}
