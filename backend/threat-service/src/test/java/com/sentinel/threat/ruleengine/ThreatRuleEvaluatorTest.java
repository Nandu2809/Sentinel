package com.sentinel.threat.ruleengine;

import com.sentinel.threat.detectors.SqlInjectionDetector;
import com.sentinel.threat.detectors.XssDetector;
import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import com.sentinel.threat.repository.RuleExecutionLogRepository;
import com.sentinel.threat.repository.ThreatRuleRepository;
import com.sentinel.threat.signatures.AttackSignatureRegistry;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThreatRuleEvaluatorTest {

    @Mock
    private ThreatRuleRepository ruleRepository;

    @Mock
    private RuleExecutionLogRepository executionLogRepository;

    private AttackSignatureRegistry signatureRegistry = new AttackSignatureRegistry();
    private ThreatRuleEvaluatorService evaluatorService;

    @BeforeEach
    void setUp() {
        SqlInjectionDetector sqliDetector = new SqlInjectionDetector(signatureRegistry);
        XssDetector xssDetector = new XssDetector(signatureRegistry);
        evaluatorService = new ThreatRuleEvaluatorService(ruleRepository, executionLogRepository, List.of(sqliDetector, xssDetector));
    }

    @Test
    void evaluate_SqlInjectionDetected() {
        ThreatRuleEntity sqliRule = new ThreatRuleEntity(
                "RULE-SQLI-001", "SQL Injection", "Detects SQLi", ThreatType.SQL_INJECTION, ThreatSeverity.CRITICAL, true, 1, "Rec"
        );
        when(ruleRepository.findByEnabledTrue()).thenReturn(List.of(sqliRule));

        RequestTelemetry telemetry = new RequestTelemetry(
                "req-1", "corr-1", "127.0.0.1", "curl", "GET", "/api/v1/users?query=1' OR '1'='1",
                Map.of(), Map.of("query", "1' OR '1'='1"), null, null, null, 200, 50L, Instant.now()
        );

        List<DetectorResult> matches = evaluatorService.evaluate(telemetry);

        assertFalse(matches.isEmpty());
        assertEquals("RULE-SQLI-001", matches.get(0).ruleCode());
        assertEquals(ThreatType.SQL_INJECTION, matches.get(0).threatType());
    }

    @Test
    void evaluate_CleanRequest_NoMatches() {
        ThreatRuleEntity sqliRule = new ThreatRuleEntity(
                "RULE-SQLI-001", "SQL Injection", "Detects SQLi", ThreatType.SQL_INJECTION, ThreatSeverity.CRITICAL, true, 1, "Rec"
        );
        when(ruleRepository.findByEnabledTrue()).thenReturn(List.of(sqliRule));

        RequestTelemetry telemetry = new RequestTelemetry(
                "req-2", "corr-2", "127.0.0.1", "Mozilla", "GET", "/api/v1/users",
                Map.of(), Map.of(), null, null, "user-123", 200, 20L, Instant.now()
        );

        List<DetectorResult> matches = evaluatorService.evaluate(telemetry);

        assertTrue(matches.isEmpty());
    }
}
