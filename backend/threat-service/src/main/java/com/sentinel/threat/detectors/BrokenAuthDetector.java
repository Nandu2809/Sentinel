package com.sentinel.threat.detectors;

import com.sentinel.threat.domain.entity.ThreatRuleEntity;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import com.sentinel.threat.ruleengine.DetectorResult;
import com.sentinel.threat.ruleengine.RequestTelemetry;
import com.sentinel.threat.ruleengine.ThreatDetector;
import org.springframework.stereotype.Component;

@Component
public class BrokenAuthDetector implements ThreatDetector {
    public static final String RULE_CODE = "RULE-AUTH-001";

    @Override
    public String getRuleCode() {
        return RULE_CODE;
    }

    @Override
    public boolean supports(RequestTelemetry telemetry) {
        return telemetry.statusCode() != null;
    }

    @Override
    public DetectorResult evaluate(RequestTelemetry telemetry, ThreatRuleEntity rule) {
        if (telemetry.statusCode() == 401 || telemetry.statusCode() == 403) {
            String uri = telemetry.uri();
            if (uri.contains("/api/v1/users") || uri.contains("/api/v1/auth")) {
                return DetectorResult.match(
                        RULE_CODE,
                        ThreatType.BROKEN_AUTH,
                        rule != null ? rule.getSeverity() : ThreatSeverity.MEDIUM,
                        "HTTP " + telemetry.statusCode(),
                        uri,
                        "Audit authentication filters and access control policies for restricted endpoints."
                );
            }
        }
        return DetectorResult.noMatch();
    }
}
