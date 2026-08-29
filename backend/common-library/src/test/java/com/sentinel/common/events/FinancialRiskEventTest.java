package com.sentinel.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FinancialRiskEventTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("FinancialRiskEvent should correctly serialize and deserialize to/from JSON")
    void testSerializationDeserialization() throws Exception {
        UUID txId = UUID.randomUUID();
        Instant now = Instant.now();

        FinancialRiskEvent original = new FinancialRiskEvent(
                txId,
                "usr_test_100",
                "merch_razorpay_soc",
                1499.50,
                "INR",
                now,
                "dev_fingerprint_abc123",
                "203.0.113.55",
                "Mumbai, IN",
                "pm_token_tok_9988",
                120,
                2,
                0,
                1,
                2
        );

        String json = objectMapper.writeValueAsString(original);
        assertNotNull(json);
        assertTrue(json.contains("usr_test_100"));
        assertTrue(json.contains("merch_razorpay_soc"));
        assertTrue(json.contains("1499.5"));

        FinancialRiskEvent deserialized = objectMapper.readValue(json, FinancialRiskEvent.class);

        assertEquals(original.transactionId(), deserialized.transactionId());
        assertEquals(original.userId(), deserialized.userId());
        assertEquals(original.merchantId(), deserialized.merchantId());
        assertEquals(original.amount(), deserialized.amount());
        assertEquals(original.currency(), deserialized.currency());
        assertEquals(original.timestamp().toEpochMilli(), deserialized.timestamp().toEpochMilli());
        assertEquals(original.deviceId(), deserialized.deviceId());
        assertEquals(original.ipAddress(), deserialized.ipAddress());
        assertEquals(original.location(), deserialized.location());
        assertEquals(original.paymentMethodRef(), deserialized.paymentMethodRef());
        assertEquals(original.accountAgeDays(), deserialized.accountAgeDays());
        assertEquals(original.velocity1h(), deserialized.velocity1h());
        assertEquals(original.failedTxCount24h(), deserialized.failedTxCount24h());
        assertEquals(original.sharedDeviceAccountCount(), deserialized.sharedDeviceAccountCount());
        assertEquals(original.sharedIpAccountCount(), deserialized.sharedIpAccountCount());
    }

    @Test
    @DisplayName("FinancialRiskEvent Record should enforce field immutability")
    void testRecordFields() {
        UUID txId = UUID.randomUUID();
        Instant now = Instant.now();

        FinancialRiskEvent event = new FinancialRiskEvent(
                txId, "usr_1", "merch_1", 500.0, "INR", now,
                "dev_1", "1.1.1.1", "Delhi, IN", "pm_1", 10, 1, 0, 1, 1
        );

        assertEquals("usr_1", event.userId());
        assertEquals(500.0, event.amount());
        assertFalse(event.toString().contains("password"));
        assertFalse(event.toString().contains("cvv"));
    }
}
