package com.sentinel.report.dto;

import com.sentinel.report.domain.model.ReportFormat;
import com.sentinel.report.domain.model.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateReportRequest(
        @NotNull(message = "Report type is required")
        ReportType reportType,

        @NotNull(message = "Report format is required")
        ReportFormat format,

        @NotBlank(message = "Created by is required")
        String createdBy
) {
}
