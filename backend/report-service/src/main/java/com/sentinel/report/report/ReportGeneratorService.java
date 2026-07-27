package com.sentinel.report.report;

import com.sentinel.common.exception.BusinessException;
import com.sentinel.common.exception.ErrorCode;
import com.sentinel.report.domain.entity.ReportHistoryEntity;
import com.sentinel.report.dto.GenerateReportRequest;
import com.sentinel.report.dto.ReportResponse;
import com.sentinel.report.export.ReportExporter;
import com.sentinel.report.mapper.ReportMapper;
import com.sentinel.report.repository.ReportHistoryRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportGeneratorService {
    private final ReportHistoryRepository reportHistoryRepository;
    private final List<ReportExporter> exporters;
    private final ReportMapper mapper;

    public ReportGeneratorService(ReportHistoryRepository reportHistoryRepository,
                                  List<ReportExporter> exporters,
                                  ReportMapper mapper) {
        this.reportHistoryRepository = reportHistoryRepository;
        this.exporters = exporters;
        this.mapper = mapper;
    }

    @Transactional
    public ReportResponse generateReport(GenerateReportRequest request) {
        String code = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String title = request.reportType().name().replace('_', ' ') + " REPORT";
        String summary = "Automated security intelligence report for " + request.reportType().name();
        String contentJson = "{\"summary\":\"All metrics normal\",\"totalThreats\":0,\"riskLevel\":\"LOW\"}";

        ReportHistoryEntity entity = new ReportHistoryEntity(
                code, request.reportType(), request.format(), title, summary, contentJson, request.createdBy()
        );

        ReportHistoryEntity saved = reportHistoryRepository.save(entity);
        return mapper.toReportResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportHistoryRepository.findAll().stream()
                .map(mapper::toReportResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(UUID id) {
        ReportHistoryEntity entity = reportHistoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Report not found: " + id));
        return mapper.toReportResponse(entity);
    }

    @Transactional(readOnly = true)
    public byte[] downloadReport(UUID id) {
        ReportHistoryEntity entity = reportHistoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Report not found: " + id));

        return exporters.stream()
                .filter(e -> e.supports(entity.getFormat()))
                .findFirst()
                .map(e -> e.export(entity))
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_ERROR, "No exporter available for format: " + entity.getFormat()));
    }
}
