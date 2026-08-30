# Sentinel — Final Security Defense

## Overview
This document provides quick responses to security, authentication, and authorization questions.

---

## 🔒 Security Defense Q&As

### 1. How is Authentication & Authorization Handled?
*"Authentication uses stateless HMAC-SHA256 JWT tokens. `auth-service` issues 15-minute tokens, and `gateway-service` validates incoming bearer headers before routing requests to backend microservices based on RBAC roles (`ROLE_USER`, `ROLE_ADMIN`)."*

### 2. How are 401 & 403 Errors Managed?
*"Unauthenticated requests to protected endpoints return standard `HTTP 401 Unauthorized`. Authorized users attempting admin operations without `ROLE_ADMIN` receive `HTTP 403 Forbidden`."*

### 3. How are Analyst Actions Audited?
*"All decision overrides, threshold adjustments, and incident status changes log immutable audit records into PostgreSQL audit tables (`risk_db.audit_logs`)."*

### 4. How is Kafka Event Security Structured?
*"In production, Kafka brokers enforce SASL/SCRAM authentication, TLS 1.3 payload encryption, and topic-level ACLs restricting write access to `FinancialRiskEventConsumer`."*

### 5. How are Production Secrets Protected?
*"Zero hardcoded production keys exist in git. Local compose fallbacks use environment variable overrides `${JWT_SECRET:...}`. Production deployments inject secrets via Kubernetes Secret objects."*

### 6. What are the Current Prototype Security Limitations?
*"Our hackathon prototype uses symmetric HMAC-SHA256 keys in local compose files. Production deployment requires asymmetric RS256 signing keys and HashiCorp Vault key rotation."*
