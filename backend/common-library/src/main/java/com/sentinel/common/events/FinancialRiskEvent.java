package com.sentinel.common.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable Canonical Financial Transaction Risk Event Envelope for Sentinel Phase 6.
 * Stores synthetic and tokenized financial transaction telemetry.
 * Strictly contains NO raw payment credentials (cards, CVVs, OTPs, or passwords).
 */
public record FinancialRiskEvent(
        UUID transactionId,
        String userId,
        String merchantId,
        Double amount,
        String currency,
        Instant timestamp,
        String deviceId,
        String ipAddress,
        String location,
        String paymentMethodRef,
        Integer accountAgeDays,
        Integer velocity1h,
        Integer failedTxCount24h,
        Integer sharedDeviceAccountCount,
        Integer sharedIpAccountCount
) {}
