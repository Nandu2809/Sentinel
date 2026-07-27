package com.sentinel.report.export;

import com.sentinel.report.domain.entity.ReportHistoryEntity;
import com.sentinel.report.domain.model.ReportFormat;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class JsonReportExporter implements ReportExporter {

    @Override
    public boolean supports(ReportFormat format) {
        return format == ReportFormat.JSON;
    }

    @Override
    public byte[] export(ReportHistoryEntity report) {
        String json = String.format("""
            {
              "reportCode": "%s",
              "type": "%s",
              "title": "%s",
              "summary": "%s",
              "generatedAt": "%s",
              "createdBy": "%s",
              "content": %s
            }
            """, report.getReportCode(), report.getReportType(), report.getTitle(),
                report.getSummary(), report.getGeneratedAt(), report.getCreatedBy(),
                report.getContentJson() != null ? report.getContentJson() : "{}");
        return json.getBytes(StandardCharsets.UTF_8);
    }
}
