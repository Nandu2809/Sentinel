package com.sentinel.report.report;

import com.sentinel.report.domain.entity.ReportHistoryEntity;
import com.sentinel.report.domain.model.ReportFormat;
import com.sentinel.report.domain.model.ReportType;
import com.sentinel.report.dto.GenerateReportRequest;
import com.sentinel.report.dto.ReportResponse;
import com.sentinel.report.export.JsonReportExporter;
import com.sentinel.report.export.ReportExporter;
import com.sentinel.report.mapper.ReportMapper;
import com.sentinel.report.repository.ReportHistoryRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportGeneratorServiceTest {

    @Mock private ReportHistoryRepository reportHistoryRepository;
    private final ReportMapper mapper = new ReportMapper();
    private ReportGeneratorService service;

    @BeforeEach
    void setUp() {
        List<ReportExporter> exporters = List.of(new JsonReportExporter());
        service = new ReportGeneratorService(reportHistoryRepository, exporters, mapper);
    }

    @Test
    void generateReport_Successful() {
        GenerateReportRequest req = new GenerateReportRequest(
                ReportType.SECURITY_SUMMARY, ReportFormat.JSON, "admin"
        );

        when(reportHistoryRepository.save(any(ReportHistoryEntity.class))).thenAnswer(i -> i.getArgument(0));

        ReportResponse response = service.generateReport(req);

        assertNotNull(response);
        assertEquals(ReportType.SECURITY_SUMMARY, response.reportType());
        assertEquals(ReportFormat.JSON, response.format());
        assertEquals("admin", response.createdBy());
    }
}
