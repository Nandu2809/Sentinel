# Sentinel — Hackathon Demo Failure & Recovery Plan

## Overview
This document specifies quick detection indicators, recovery commands, backup demo paths, and spoken responses for 10 potential live demo failure scenarios.

> 🚨 **GOLDEN RULE**: NEVER fake a live result. If a live container or network connection fails, be transparent, execute the quick recovery command, or switch seamlessly to offline fallback providers.

---

## 🛠️ Failure Matrix & Recovery Protocols

### 1. Kafka Container Unavailable
- **Detection**: Stream table doesn't auto-poll live events; header displays `KAFKA: DISCONNECTED`.
- **Immediate Recovery Command**: `docker restart sentinel-kafka sentinel-zookeeper`
- **Backup Demo Path**: Scenario Quick-Testers use in-memory Angular fallback providers (`FinancialRiskService`) to render decisions seamlessly.
- **Spoken Response**: *"Our live Kafka broker experienced a transient connection delay; our Angular service seamlessly switched to in-memory event fallback without dropping SOC visibility."*

---

### 2. AI Engine Unavailable (FastAPI Port 8000)
- **Detection**: `/api/v1/ai/status` returns connection refused or 500 error.
- **Immediate Recovery Command**: `docker restart sentinel-ai-engine`
- **Backup Demo Path**: `risk-service` invokes deterministic heuristic fallback decision provider (`evaluateFinancialRisk`).
- **Spoken Response**: *"The standalone Python AI engine is recovering; our Java risk service automatically engaged heuristic rule fallback to ensure zero downtime."*

---

### 3. API Gateway Service Unavailable (Port 8088 / 8080)
- **Detection**: Frontend API requests fail with 502 Bad Gateway or CORS errors.
- **Immediate Recovery Command**: `docker restart sentinel-gateway-service`
- **Backup Demo Path**: Demonstrate offline policy simulator and SVG graph topology directly in frontend component.
- **Spoken Response**: *"The edge gateway is re-establishing token bucket rate limits; let's inspect our frontend topology engine and policy simulator."*

---

### 4. Frontend API Timeout
- **Detection**: Spinner stays active on `REFRESH` click.
- **Immediate Recovery Command**: Refresh browser page (`Ctrl + F5`).
- **Backup Demo Path**: Click pre-populated `SCENARIO A`, `SCENARIO B`, or `SCENARIO C` buttons which inject local decision data.
- **Spoken Response**: *"Refreshing client state—let's trigger our pre-loaded Scenario C fraud ring evaluation."*

---

### 5. PostgreSQL Database Connection Down
- **Detection**: `auth-service` or `risk-service` logs exhibit database connection timeout.
- **Immediate Recovery Command**: `docker restart sentinel-postgres`
- **Backup Demo Path**: Demonstrate stream evaluation and Explainable AI panel using transient UI memory.
- **Spoken Response**: *"PostgreSQL schema connection is re-binding; transaction evaluation and Explainable AI run in active memory."*

---

### 6. WebSocket Stream Disconnected
- **Detection**: Live activity log console feed stops scrolling.
- **Immediate Recovery Command**: Click `↻ REFRESH` button on header banner.
- **Backup Demo Path**: Trigger new scenario evaluation button to force activity log emission.
- **Spoken Response**: *"Re-subscribing client to activity console feed."*

---

### 7. Browser Refresh / Hard Crash
- **Detection**: Browser crashes or blank page shown.
- **Immediate Recovery Command**: Open new tab to `http://localhost:4200/financial-risk`.
- **Backup Demo Path**: Pre-built production bundle in `dist/sentinel` loads instantaneously.
- **Spoken Response**: *"Re-opening our Angular 17 Command Center workspace."*

---

### 8. Authentication Failure / Expired Token
- **Detection**: 401 Unauthorized redirect to `/login`.
- **Immediate Recovery Command**: Log in with demo credentials (`admin@sentinel.io` / `admin123`).
- **Backup Demo Path**: Bypass login by directly navigating to `/financial-risk` route.
- **Spoken Response**: *"Session security token refreshed; logging in with RBAC Administrator credentials."*

---

### 9. Incident Not Created automatically
- **Detection**: Clicking `INCIDENT` button in stream table doesn't open modal drawer.
- **Immediate Recovery Command**: Click `🚨 OPEN INCIDENT WORKSPACE` in investigation drawer or navigate directly to `/incidents`.
- **Backup Demo Path**: Navigate directly to `/incidents` route via left navigation rail (`INC`).
- **Spoken Response**: *"Navigating directly to our SOC Incident Response workspace at /incidents."*

---

### 10. Mailpit SMTP Container Unavailable
- **Detection**: Escalation email not visible in Mailpit UI (`http://localhost:8025`).
- **Immediate Recovery Command**: `docker restart sentinel-mailpit`
- **Backup Demo Path**: Show automated incident status transition (`NEW` $\rightarrow$ `ACKNOWLEDGED` $\rightarrow$ `RESOLVED`) in `/incidents` table.
- **Spoken Response**: *"SLA escalation alerts are logged in PostgreSQL audit tables and visible in our incident lifecycle view."*
