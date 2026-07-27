package com.sentinel.report.dto;

import com.sentinel.report.domain.model.ReportFormat;
import com.sentinel.report.domain.model.ReportType;
import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        String reportCode,
        ReportType reportType,
        ReportFormat format,
        String title,
        String summary,
        String contentJson,
        String createdBy,
        Instant generatedAt
) {
}
