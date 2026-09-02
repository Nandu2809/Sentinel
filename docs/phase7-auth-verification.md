# Sentinel Phase 7 — Authentication & Gateway Route Verification

**Generated:** 2026-09-02  
**API Gateway Base URL:** `http://localhost:8088`  
**Auth Microservice:** `http://localhost:8081`

## Verification Matrix

| Request Scenario | Endpoint | Authorization Header | Status Code Received | Expected Behavior | Verification Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Authentication Login** | `POST /api/v1/auth/login` | None (Public) | **200 OK** | JWT Access Token returned | **PASS** |
| **Protected Route (No Token)** | `GET /api/v1/financial-risk/summary` | None | **401 Unauthorized** | Gateway rejects request | **PASS** |
| **Protected Route (Invalid Token)** | `GET /api/v1/financial-risk/summary` | `Bearer invalid.jwt.signature` | **401 Unauthorized** | Gateway validates & rejects malformed token | **PASS** |
| **Protected Route (Valid Token)** | `GET /api/v1/financial-risk/summary` | `Bearer <VALID_JWT_TOKEN>` | **200 OK** | Access granted, protected payload returned | **PASS** |

## JWT Structure & Claims Verification
The generated JWT access token was decoded and verified for the following mandatory security claims:
- `iss`: `sentinel-auth-service`
- `sub`: User UUID
- `email`: `admin@sentinel.com`
- `username`: `sentineladmin`
- `roles`: `["USER"]`
- `permissions`: `["AUTH_SELF"]`
- `iat` / `exp`: Valid 15-minute expiration window

## Semantic Behavior Note
As requested, authentication failures (missing token or malformed JWT) return HTTP 401 (Unauthorized), which is semantically correct for token-based gateway authentication under Spring Cloud Gateway's `JwtAuthenticationFilter`.
