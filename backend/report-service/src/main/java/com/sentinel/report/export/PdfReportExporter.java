package com.sentinel.report.export;

import com.sentinel.report.domain.entity.ReportHistoryEntity;
import com.sentinel.report.domain.model.ReportFormat;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class PdfReportExporter implements ReportExporter {

    @Override
    public boolean supports(ReportFormat format) {
        return format == ReportFormat.PDF;
    }

    @Override
    public byte[] export(ReportHistoryEntity report) {
        String pdfMockText = String.format("""
            %PDF-1.4
            1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj
            2 0 obj << /Type /Pages /Kinds [3 0 R] /Count 1 >> endobj
            3 0 obj << /Type /Page /Parent 2 0 R /Contents 4 0 R >> endobj
            4 0 obj << /Length 120 >> stream
            BT /F1 12 Tf 50 700 TD (%s - %s) Tj ET
            endstream endobj
            xref
            trailer << /Root 1 0 R >>
            %%EOF
            """, report.getReportCode(), report.getTitle());
        return pdfMockText.getBytes(StandardCharsets.UTF_8);
    }
}
