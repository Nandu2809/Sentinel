package com.sentinel.report.export;

import com.sentinel.report.domain.entity.ReportHistoryEntity;
import com.sentinel.report.domain.model.ReportFormat;

public interface ReportExporter {
    boolean supports(ReportFormat format);
    byte[] export(ReportHistoryEntity report);
}
