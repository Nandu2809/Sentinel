package com.sentinel.threat.service;

import com.sentinel.common.events.SecurityEventEnvelope;
import com.sentinel.common.security.SecurityEventType;
import com.sentinel.threat.domain.entity.ThreatEventEntity;
import com.sentinel.threat.domain.model.ThreatSeverity;
import com.sentinel.threat.domain.model.ThreatType;
import com.sentinel.threat.repository.ThreatEventRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ThreatDetectionService {
    private static final Logger log = LoggerFactory.getLogger(ThreatDetectionService.class);
    private static final int BRUTE_FORCE_THRESHOLD = 5;
    private static final long TIME_WINDOW_MINUTES = 5;

    private final ThreatEventRepository threatEventRepository;
    private final Map<String, Queue<Instant>> failedAttemptsMap = new ConcurrentHashMap<>();

    public ThreatDetectionService(ThreatEventRepository threatEventRepository) {
        this.threatEventRepository = threatEventRepository;
    }

    @Transactional
    public void analyze(SecurityEventEnvelope event) {
        if (event == null || event.eventType() == null) {
            return;
        }

        String userKey = resolveUserKey(event);
        if (userKey == null) {
            return;
        }

        if (event.eventType() == SecurityEventType.LOGIN_FAILED) {
            handleFailedLogin(event, userKey);
        } else if (event.eventType() == SecurityEventType.LOGIN_SUCCEEDED) {
            handleSuccessfulLogin(event, userKey);
        }
    }

    private void handleFailedLogin(SecurityEventEnvelope event, String userKey) {
        Instant now = event.timestamp() != null ? event.timestamp() : Instant.now();
        Queue<Instant> timestamps = failedAttemptsMap.computeIfAbsent(userKey, k -> new ConcurrentLinkedQueue<>());
        
        timestamps.add(now);
        evictExpiredTimestamps(timestamps, now);

        if (timestamps.size() >= BRUTE_FORCE_THRESHOLD) {
            ThreatEventEntity threat = new ThreatEventEntity(
                    event.eventId(),
                    ThreatType.BRUTE_FORCE_ATTACK,
                    ThreatSeverity.HIGH,
                    userKey,
                    event.ipAddress(),
                    "Brute force attack detected: " + timestamps.size() + " failed login attempts within " + TIME_WINDOW_MINUTES + " minutes",
                    85.0,
                    now
            );

            threatEventRepository.save(threat);
            log.info("THREAT_DETECTED type=BRUTE_FORCE_ATTACK riskScore=85 user={} ip={}", userKey, event.ipAddress());
        }
    }

    private void handleSuccessfulLogin(SecurityEventEnvelope event, String userKey) {
        Instant now = event.timestamp() != null ? event.timestamp() : Instant.now();
        Queue<Instant> timestamps = failedAttemptsMap.get(userKey);

        if (timestamps != null) {
            evictExpiredTimestamps(timestamps, now);
            if (!timestamps.isEmpty()) {
                ThreatEventEntity threat = new ThreatEventEntity(
                        event.eventId(),
                        ThreatType.SUSPICIOUS_LOGIN,
                        ThreatSeverity.MEDIUM,
                        userKey,
                        event.ipAddress(),
                        "Suspicious login: successful login following " + timestamps.size() + " recent failed attempts",
                        60.0,
                        now
                );

                threatEventRepository.save(threat);
                log.info("THREAT_DETECTED type=SUSPICIOUS_LOGIN riskScore=60 user={} ip={}", userKey, event.ipAddress());
                failedAttemptsMap.remove(userKey);
            }
        }
    }

    private void evictExpiredTimestamps(Queue<Instant> timestamps, Instant referenceTime) {
        Instant cutoff = referenceTime.minus(TIME_WINDOW_MINUTES, ChronoUnit.MINUTES);
        timestamps.removeIf(timestamp -> timestamp.isBefore(cutoff));
    }

    private String resolveUserKey(SecurityEventEnvelope event) {
        if (event.email() != null && !event.email().isBlank()) {
            return event.email().toLowerCase();
        }
        if (event.userId() != null) {
            return event.userId().toString();
        }
        return null;
    }
}
