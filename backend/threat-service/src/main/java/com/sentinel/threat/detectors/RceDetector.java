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
public class RceDetector implements ThreatDetector {
    public static final String RULE_CODE = "RULE-RCE-001";
    private final AttackSignatureRegistry signatureRegistry;

    public RceDetector(AttackSignatureRegistry signatureRegistry) {
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
        String match = signatureRegistry.matchRce(telemetry.body());
        if (match == null && telemetry.uri() != null) {
            match = signatureRegistry.matchRce(telemetry.uri());
        }

        if (match != null) {
            return DetectorResult.match(
                    RULE_CODE,
                    ThreatType.RCE,
                    rule != null ? rule.getSeverity() : ThreatSeverity.CRITICAL,
                    match,
                    match,
                    "Block unsafe deserialization and dynamic runtime reflection calls."
            );
        }
        return DetectorResult.noMatch();
    }
}
