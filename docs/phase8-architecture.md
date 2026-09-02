# Sentinel Phase 8 — System Architecture & Data Flow Diagram

## Executive Overview
Sentinel is structured as a cloud-native microservices architecture designed to decouple transaction ingestion, risk evaluation, machine learning inference, and SOC incident escalation.

---

## 🏗️ End-to-End System Architecture

```
                         ┌──────────────────┐
                         │  Angular Frontend │
                         │   Judge / SOC UI  │
                         └─────────┬────────┘
                                   │ (HTTP / WebSocket)
                                   ▼
                         ┌──────────────────┐
                         │    API Gateway   │
                         │      :8088       │
                         └─────────┬────────┘
                                   │
          ┌────────────────────────┼────────────────────────┐
          │                        │                        │
          ▼                        ▼                        ▼
    Auth Service            Financial Risk            SOC Services
       :8081                    :8084              Alert / Incident
                                  │               (:8085 / :8083)
                                  ▼
                         ┌──────────────────┐
                         │    AI Engine     │
                         │      :8000       │
                         └─────────┬────────┘
                                   │
                                   ▼
                         ┌──────────────────┐
                         │ Relationship     │
                         │ Graph Intelligence│
                         └─────────┬────────┘
                                   │
                                   ▼
                         ┌──────────────────┐
                         │ Cost-Aware Risk  │
                         │ Decision Engine  │
                         └─────┬────┬───────┘
                               │    │
                         APPROVE│    │BLOCK
                               │    │
                               ▼    ▼
                            REVIEW ALERT
                                   │
                                   ▼
                               INCIDENT (INC-2026-XXXX)
                                   │
                                   ▼
                            THREAT HUNTING
                                   │
                                   ▼
                         Mailpit Notifications (:8025)
```

---

## 🔄 Complete 11-Stage Pipeline Data Flow

1. **Transaction Telemetry Arrival**: Payment event arrives via REST API Gateway (:8088) or Kafka topic `sentinel.financial.events`.
2. **Gateway Verification**: `JwtAuthenticationFilter` validates JWT bearer token and enforces CORS.
3. **Financial Risk Event Ingestion**: `FinancialRiskController` parses payload into canonical `FinancialRiskEvent` record.
4. **Feature Extraction**: Computes 34 total features (9 baseline velocity/amount features + 25 graph topology features).
5. **Entity Graph Traversal**: In-memory graph traverses 1-hop, 2-hop, and 3-hop relationships connecting `USER`, `DEVICE`, `IP_ADDRESS`, `PAYMENT_REF`, and `MERCHANT`.
6. **AI Engine Inference**: REST call to Python AI Engine (:8000) evaluates Random Forest classifier model to output risk score $p \in [0, 100]$.
7. **Cost-Aware Policy Evaluation**: Applies asymmetric business cost policy ($\tau_{\text{block}} = 0.50$, $\tau_{\text{review}} = 0.25$).
8. **Automated Action**:
   - `APPROVE` ($p < 25$): Logged as clean transaction.
   - `REVIEW` ($25 \le p < 50$): Flagged for human analyst review.
   - `BLOCK` ($p \ge 50$): Automated transaction block.
9. **Kafka Alert Broadcast**: High-risk events publish `AlertEvent` to `sentinel.alert-events` topic.
10. **Incident Creation**: `alert-service` auto-creates structured security Incident (`INC-2026-XXXX`) with timeline evidence.
11. **Email Notification & SOC Workstation**: Triggers SMTP notification to Mailpit (:8025) and renders real-time update on Angular SOC Workstation (`/financial-risk`, `/incidents`, `/threat-hunting`).

---

## 🗄️ Infrastructure & Persistence Layer
- **PostgreSQL 16** (:5432): Relational persistence for accounts, transactions, risk decisions, alerts, incidents, and audit trails.
- **Redis 7** (:6379): Low-latency velocity cache and session management.
- **Apache Kafka & Zookeeper** (:9092 / :2181): Asynchronous event streaming bus.
- **Mailpit** (:8025): Local webmail server for SMTP alert delivery.
- **Prometheus & Grafana** (:9090 / :3000): System telemetry and metrics visualization.
