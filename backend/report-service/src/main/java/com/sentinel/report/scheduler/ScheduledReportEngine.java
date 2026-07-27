package com.sentinel.report.scheduler;

import com.sentinel.report.domain.entity.ScheduledReportEntity;
import com.sentinel.report.dto.GenerateReportRequest;
import com.sentinel.report.report.ReportGeneratorService;
import com.sentinel.report.repository.ScheduledReportRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScheduledReportEngine {
    private static final Logger log = LoggerFactory.getLogger(ScheduledReportEngine.class);
    private final ScheduledReportRepository scheduledReportRepository;
    private final ReportGeneratorService reportGeneratorService;

    public ScheduledReportEngine(ScheduledReportRepository scheduledReportRepository,
                                 ReportGeneratorService reportGeneratorService) {
        this.scheduledReportRepository = scheduledReportRepository;
        this.reportGeneratorService = reportGeneratorService;
    }

    @Scheduled(cron = "0 0 1 * * ?") // Daily at 1:00 AM
    @Transactional
    public void executeScheduledReports() {
        List<ScheduledReportEntity> jobs = scheduledReportRepository.findByEnabledTrue();
        for (ScheduledReportEntity job : jobs) {
            log.info("executing_scheduled_report jobName={} type={}", job.getJobName(), job.getReportType());
            GenerateReportRequest req = new GenerateReportRequest(
                    job.getReportType(), job.getFormat(), "SCHEDULED_JOB"
            );
            reportGeneratorService.generateReport(req);
            job.markExecuted();
            scheduledReportRepository.save(job);
        }
    }
}
