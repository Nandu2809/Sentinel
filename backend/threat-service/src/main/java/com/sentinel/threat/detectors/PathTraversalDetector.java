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
public class PathTraversalDetector implements ThreatDetector {
    public static final String RULE_CODE = "RULE-TRAV-001";
    private final AttackSignatureRegistry signatureRegistry;

    public PathTraversalDetector(AttackSignatureRegistry signatureRegistry) {
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
        String match = signatureRegistry.matchPathTraversal(telemetry.uri());
        if (match == null && telemetry.queryParams() != null) {
            match = signatureRegistry.matchPathTraversal(telemetry.queryParams().toString());
        }

        if (match != null) {
            return DetectorResult.match(
                    RULE_CODE,
                    ThreatType.PATH_TRAVERSAL,
                    rule != null ? rule.getSeverity() : ThreatSeverity.HIGH,
                    match,
                    match,
                    "Restrict filesystem path resolution and enforce canonical path checks."
            );
        }
        return DetectorResult.noMatch();
    }
}
