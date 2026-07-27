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
public class BotDetectionDetector implements ThreatDetector {
    public static final String RULE_CODE = "RULE-BOT-001";
    private final AttackSignatureRegistry signatureRegistry;

    public BotDetectionDetector(AttackSignatureRegistry signatureRegistry) {
        this.signatureRegistry = signatureRegistry;
    }

    @Override
    public String getRuleCode() {
        return RULE_CODE;
    }

    @Override
    public boolean supports(RequestTelemetry telemetry) {
        return telemetry.userAgent() != null;
    }

    @Override
    public DetectorResult evaluate(RequestTelemetry telemetry, ThreatRuleEntity rule) {
        String match = signatureRegistry.matchBotUserAgent(telemetry.userAgent());
        if (match != null) {
            return DetectorResult.match(
                    RULE_CODE,
                    ThreatType.BOT_DETECTION,
                    rule != null ? rule.getSeverity() : ThreatSeverity.MEDIUM,
                    match,
                    telemetry.userAgent(),
                    "Block automated security scanner user agents and enforce CAPTCHA or rate limits."
            );
        }
        return DetectorResult.noMatch();
    }
}
