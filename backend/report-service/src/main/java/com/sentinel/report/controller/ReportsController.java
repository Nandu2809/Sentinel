package com.sentinel.report.controller;

import com.sentinel.common.api.ApiResponse;
import com.sentinel.report.dto.GenerateReportRequest;
import com.sentinel.report.dto.ReportResponse;
import com.sentinel.report.report.ReportGeneratorService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportsController {
    private final ReportGeneratorService reportGeneratorService;

    public ReportsController(ReportGeneratorService reportGeneratorService) {
        this.reportGeneratorService = reportGeneratorService;
    }

    @PostMapping("/generate")
    public ApiResponse<ReportResponse> generateReport(@Valid @RequestBody GenerateReportRequest request) {
        return ApiResponse.success(HttpStatus.CREATED.value(), "Report generation initiated", reportGeneratorService.generateReport(request));
    }

    @GetMapping
    public ApiResponse<List<ReportResponse>> getAllReports() {
        return ApiResponse.success(HttpStatus.OK.value(), "Reports list retrieved", reportGeneratorService.getAllReports());
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportResponse> getReportById(@PathVariable UUID id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Report details retrieved", reportGeneratorService.getReportById(id));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable UUID id) {
        byte[] content = reportGeneratorService.downloadReport(id);
        ReportResponse report = reportGeneratorService.getReportById(id);

        String filename = report.reportCode() + "." + report.format().name().toLowerCase();
        MediaType mediaType = report.format().name().equalsIgnoreCase("PDF") ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(content);
    }
}
