package com.sentinel.common.constants;

/**
 * Centralized Kafka Topic Constants for Sentinel Microservices.
 */
public final class KafkaConstants {
    private KafkaConstants() {}

    public static final String SECURITY_EVENTS_TOPIC = "sentinel.security-events";
    public static final String THREAT_EVENTS_TOPIC = "sentinel.threat-events";
    public static final String RISK_EVENTS_TOPIC = "sentinel.risk-events";
    public static final String ALERT_EVENTS_TOPIC = "sentinel.alert-events";
    public static final String FINANCIAL_EVENTS_TOPIC = "sentinel.financial.events";
}
