package com.sentinel.threat.ruleengine;

import com.sentinel.threat.domain.entity.ThreatRuleEntity;

public interface ThreatDetector {
    String getRuleCode();
    boolean supports(RequestTelemetry telemetry);
    DetectorResult evaluate(RequestTelemetry telemetry, ThreatRuleEntity rule);
}
