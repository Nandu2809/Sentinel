# Sentinel Phase 6H — End-to-End System Architecture Audit

## 1. Executive Summary

Sentinel is an enterprise **AI Risk Manager and Security Intelligence Platform** built for real-time transaction abuse prevention, API security monitoring, and SOC incident management.

This audit documents the complete end-to-end event-driven architecture, microservice responsibilities, data flow, Kafka topics, REST endpoints, and database schemas verified across Phase 1–6H.

---

## 2. End-to-End Financial Risk Data Flow Architecture

```
[Financial Transaction]
        │
        ▼
FinancialRiskEvent (Canonical Record Envelope)
        │
        ▼
Kafka Topic: sentinel.financial.events
        │
        ▼
FinancialRiskEventConsumer (risk-service)
        │
        ├───────────────────────────────────────────┐
        ▼                                           ▼
Feature Extraction (34 Features)         In-Memory Topology Graph
(Baseline 9 + Enhanced 25)               (Users, Devices, IPs, Tokens)
        │                                           │
        └─────────────────────┬─────────────────────┘
                              ▼
                   Baseline & Enhanced ML Models
                              │
                              ▼
                     Cost-Aware Decision Engine
                (APPROVE < 0.25 <= REVIEW < 0.50 <= BLOCK)
                              │
                              ▼
                     RiskAssessmentEntity (PostgreSQL)
                              │
                              ▼
                    Kafka Topic: sentinel.risk-events
                              │
                              ▼
               Kafka Topic: sentinel.alert-events (if HIGH/CRITICAL)
                              │
                              ▼
                 AlertService & IncidentService (Phase 5)
                              │
                              ▼
              Sentinel Angular SOC Command Center (/financial-risk)
```

---

## 3. Microservice & Component Audit Table

| Component | Primary Responsibility | Input / Trigger | Output / Artifact | Comm. Protocol | Storage | Status | Known Limitations |
| :--- | :--- | :--- | :--- | :--- | :--- | :---: | :--- |
| **Gateway Service** (`gateway-service`) | API Gateway, Route Dispatching, JWT Validation | HTTP Requests (`:8088`) | Proxied HTTP Headers | REST / Reactive Netty | Memory | **LIVE** | Port 8088 routing requires active downstream services |
| **Auth Service** (`auth-service`) | User Auth, JWT Issuance, Role Guards | `/api/v1/auth/*` | Signed JWT Access Tokens | REST | PostgreSQL (`auth_db`) | **LIVE** | Token revocation requires Redis blacklist sync |
| **Monitoring Service** (`monitoring-service`) | System Telemetry, Audit Logs, OpenApi Registry | `/api/v1/monitoring/*` | Audit Event Logs | REST | PostgreSQL (`monitoring_db`) | **LIVE** | Retention policies enforce 90-day pruning |
| **Threat Service** (`threat-service`) | API Threat Detection (XSS, SQLi, Brute Force) | `/api/v1/threats/*` | `ThreatEventEnvelope` | REST / Kafka | PostgreSQL (`threat_db`) | **LIVE** | Rule engine uses regex signature matching |
| **Risk Service** (`risk-service`) | Financial Risk, Composite Scoring, Cost Decisions | `/api/v1/financial-risk/*`, Kafka Events | `RiskAssessmentEntity`, `AlertEventEnvelope` | REST / Kafka (`sentinel.financial.events`) | PostgreSQL (`risk_db`) | **LIVE** | Topology features rely on sliding time window |
| **Alert Service** (`alert-service`) | Alert Dispatch, Severity Filtering | `sentinel.alert-events` | Dispatched Alerts | Kafka / REST | PostgreSQL (`alert_db`) | **LIVE** | Dispatch notifications logged to console |
| **Incident Service** (`incidents`) | SOC Incident Response & Investigation Workspace | `/api/v1/incidents/*` | Incident Workspaces | REST | PostgreSQL (`incident_db`) | **LIVE** | Incident assignment requires active SOC analyst role |
| **Report Service** (`report-service`) | Executive Security & Risk Report Generation | `/api/v1/reports/*` | PDF / JSON Reports | REST | PostgreSQL | **LIVE** | Report compilation rendered asynchronously |
| **AI Engine** (`ai-engine`) | PyTorch Anomaly Detection, Financial ML Pipeline | Scikit-Learn / PyTorch Models | Anomaly & Risk Probability | Python gRPC / REST | Data Files (`data/financial/`) | **LIVE** | Synthetic training dataset fit on 10,000 transactions |
| **Angular SOC UI** (`frontend/sentinel`) | Cyber Intelligence SOC Command Center | Browser HTTP (`:4200` / `/financial-risk`) | Interactive SOC Workstation | Angular 17 / RxJS | Browser Memory | **LIVE** | Desktop SOC layout optimized for >= 1280px |

---

## 4. Kafka Event Stream Topics

| Kafka Topic | Producer Service | Consumer Service(s) | Payload Contract | Description |
| :--- | :--- | :--- | :--- | :--- |
| `sentinel.security-events` | Gateway / Threat Service | Monitoring Service | `SecurityEventEnvelope` | Ingests raw security telemetry |
| `sentinel.threat-events` | Threat Service | Risk Service | `ThreatEventEnvelope` | Ingests evaluated security threat signals |
| `sentinel.ai-events` | AI Engine | Risk Service | `AIThreatEventEnvelope` | Ingests AI anomaly detection scores |
| `sentinel.financial.events` | Financial Gateway / External | Risk Service | `FinancialRiskEvent` | Canonical transaction telemetry envelope |
| `sentinel.risk-events` | Risk Service | Alert / Report Services | `RiskEventEnvelope` | Evaluated risk score & decision envelope |
| `sentinel.alert-events` | Risk Service | Alert / Incident Services | `AlertEventEnvelope` | Dispatched alerts crossing risk thresholds |

---

## 5. Security & Authentication Model

1. **JWT Authentication**: Secured via RSA/HS256 signature validation at Gateway level.
2. **Role-Based Access Control (RBAC)**: Enforces `USER`, `ANALYST`, and `ADMIN` role permissions across API routes.
3. **Data Protection**: Zero raw credit card numbers, CVVs, OTPs, or passwords in telemetry envelopes (`FinancialRiskEvent` uses tokenized card references).
