package com.sentinel.threat.detectors;

import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import com.sentinel.threat.ruleengine.DetectorResult;
import com.sentinel.threat.ruleengine.RequestTelemetry;
import com.sentinel.threat.ruleengine.ThreatDetector;
import com.sentinel.threat.signatures.AttackSignatureRegistry;
import org.springframework.stereotype.Component;

@Component
public class SqlInjectionDetector implements ThreatDetector {
    public static final String RULE_CODE = "RULE-SQLI-001";
    private final AttackSignatureRegistry signatureRegistry;

    public SqlInjectionDetector(AttackSignatureRegistry signatureRegistry) {
        this.signatureRegistry = signatureRegistry;
    }

    @Override
    public String getRuleCode() {
        return RULE_CODE;
    }

    @Override
    public boolean supports(RequestTelemetry telemetry) {
        return true;
    }

    @Override
    public DetectorResult evaluate(RequestTelemetry telemetry, ThreatRuleEntity rule) {
        String match = signatureRegistry.matchSqlInjection(telemetry.uri());
        if (match == null && telemetry.queryParams() != null) {
            match = signatureRegistry.matchSqlInjection(telemetry.queryParams().toString());
        }
        if (match == null && telemetry.body() != null) {
            match = signatureRegistry.matchSqlInjection(telemetry.body());
        }

        if (match != null) {
            return DetectorResult.match(
                    RULE_CODE,
                    ThreatType.SQL_INJECTION,
                    rule != null ? rule.getSeverity() : ThreatSeverity.CRITICAL,
                    match,
                    match,
                    "Enforce SQL parameter binding, input validation, and WAF inspection."
            );
        }
        return DetectorResult.noMatch();
    }
}
