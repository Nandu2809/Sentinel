# Sentinel Penetration Testing Framework & Report

## 1. Scope & Objective
Simulated adversarial attacks against Sentinel API Gateway, Authentication Service, Threat Engine, and AI Service to verify defensive resiliency.

---

## 2. Attack Simulation Test Scenarios

### Scenario 1: JWT Signature Forgery & Alg None Attack
- **Vector**: Modifying JWT header to `{"alg": "none"}` or signing with a weak secret.
- **Expected Outcome**: Gateway rejects token with HTTP 401 Unauthorized.
- **Result**: **PASSED**. Signature verification strictly enforced by `JwtValidator`.

### Scenario 2: SQL Injection (SQLi) in Query Parameters
- **Vector**: Sending payload `GET /api/v1/alerts?status=NEW' OR '1'='1`
- **Expected Outcome**: Prepared statements prevent injection; Threat Engine flags SQLi attempt.
- **Result**: **PASSED**. Hibernate/JPA parameterized queries prevent database corruption.

### Scenario 3: Brute Force Authentication & Password Spray
- **Vector**: 500 rapid login requests targeting `/api/v1/auth/login`.
- **Expected Outcome**: Rate limiter blocks IP after threshold; Auth service delays response.
- **Result**: **PASSED**. Redis Rate Limiter blocks excessive requests with HTTP 429.

### Scenario 4: AI Engine Adversarial Payload Obfuscation
- **Vector**: Sending obfuscated URI payloads to test Shannon entropy anomaly detection.
- **Expected Outcome**: AI Engine extracts entropy feature and flags anomaly with >85 score.
- **Result**: **PASSED**. XAI Explainer correctly generated entropy deviation reason.
