package com.sentinel.risk.controller;

import com.sentinel.common.events.FinancialRiskEvent;
import com.sentinel.risk.domain.entity.RiskAssessmentEntity;
import com.sentinel.risk.repository.RiskAssessmentRepository;
import com.sentinel.risk.service.RiskCalculationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sentinel Phase 6F — Financial Risk REST API Controller.
 */
@RestController
@RequestMapping("/api/v1/financial-risk")
public class FinancialRiskController {
    private static final Logger log = LoggerFactory.getLogger(FinancialRiskController.class);

    private final RiskCalculationService riskCalculationService;
    private final RiskAssessmentRepository riskAssessmentRepository;

    public FinancialRiskController(RiskCalculationService riskCalculationService,
                                   RiskAssessmentRepository riskAssessmentRepository) {
        this.riskCalculationService = riskCalculationService;
        this.riskAssessmentRepository = riskAssessmentRepository;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Map<String, Object>> evaluate(@RequestBody FinancialRiskEvent event) {
        log.info("Received HTTP request to evaluate FinancialRiskEvent txId={}", event.transactionId());
        riskCalculationService.evaluateFinancialRisk(event);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("transactionId", event.transactionId());
        response.put("message", "FinancialRiskEvent evaluated successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/decisions")
    public ResponseEntity<List<RiskAssessmentEntity>> getRecentDecisions() {
        List<RiskAssessmentEntity> assessments = riskAssessmentRepository.findAll();
        return ResponseEntity.ok(assessments);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        List<RiskAssessmentEntity> assessments = riskAssessmentRepository.findAll();

        long approved = assessments.stream().filter(a -> "APPROVE".equalsIgnoreCase(a.getDecision())).count();
        long reviewed = assessments.stream().filter(a -> "REVIEW".equalsIgnoreCase(a.getDecision())).count();
        long blocked = assessments.stream().filter(a -> "BLOCK".equalsIgnoreCase(a.getDecision()) || "BLOCK_AND_ALERT".equalsIgnoreCase(a.getDecision())).count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEvaluated", assessments.size());
        summary.put("approveCount", approved);
        summary.put("reviewCount", reviewed);
        summary.put("blockCount", blocked);
        return ResponseEntity.ok(summary);
    }
}
