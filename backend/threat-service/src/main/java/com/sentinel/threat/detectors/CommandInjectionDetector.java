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
public class CommandInjectionDetector implements ThreatDetector {
    public static final String RULE_CODE = "RULE-CMDI-001";
    private final AttackSignatureRegistry signatureRegistry;

    public CommandInjectionDetector(AttackSignatureRegistry signatureRegistry) {
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
        String match = signatureRegistry.matchCommandInjection(telemetry.uri());
        if (match == null && telemetry.queryParams() != null) {
            match = signatureRegistry.matchCommandInjection(telemetry.queryParams().toString());
        }
        if (match == null && telemetry.body() != null) {
            match = signatureRegistry.matchCommandInjection(telemetry.body());
        }

        if (match != null) {
            return DetectorResult.match(
                    RULE_CODE,
                    ThreatType.COMMAND_INJECTION,
                    rule != null ? rule.getSeverity() : ThreatSeverity.CRITICAL,
                    match,
                    match,
                    "Disable direct shell execution and sanitize user command arguments."
            );
        }
        return DetectorResult.noMatch();
    }
}
