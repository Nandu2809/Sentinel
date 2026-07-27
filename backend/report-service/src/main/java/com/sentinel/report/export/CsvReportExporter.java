package com.sentinel.report.export;

import com.sentinel.report.domain.entity.ReportHistoryEntity;
import com.sentinel.report.domain.model.ReportFormat;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class CsvReportExporter implements ReportExporter {

    @Override
    public boolean supports(ReportFormat format) {
        return format == ReportFormat.CSV;
    }

    @Override
    public byte[] export(ReportHistoryEntity report) {
        String csv = String.format("""
            ReportCode,ReportType,Format,Title,GeneratedAt,CreatedBy
            "%s","%s","%s","%s","%s","%s"
            """, report.getReportCode(), report.getReportType(), report.getFormat(),
                report.getTitle(), report.getGeneratedAt(), report.getCreatedBy());
        return csv.getBytes(StandardCharsets.UTF_8);
    }
}
