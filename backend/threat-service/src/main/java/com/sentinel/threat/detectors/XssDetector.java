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
public class XssDetector implements ThreatDetector {
    public static final String RULE_CODE = "RULE-XSS-001";
    private final AttackSignatureRegistry signatureRegistry;

    public XssDetector(AttackSignatureRegistry signatureRegistry) {
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
        String match = signatureRegistry.matchXss(telemetry.uri());
        if (match == null && telemetry.queryParams() != null) {
            match = signatureRegistry.matchXss(telemetry.queryParams().toString());
        }
        if (match == null && telemetry.body() != null) {
            match = signatureRegistry.matchXss(telemetry.body());
        }

        if (match != null) {
            return DetectorResult.match(
                    RULE_CODE,
                    ThreatType.XSS,
                    rule != null ? rule.getSeverity() : ThreatSeverity.HIGH,
                    match,
                    match,
                    "Sanitize input parameters and enforce Content Security Policy (CSP) headers."
            );
        }
        return DetectorResult.noMatch();
    }
}
