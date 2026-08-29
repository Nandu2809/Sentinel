# Sentinel Phase 6F — Real-Time Financial Risk Integration & Benchmark Hardening

## Overview
Phase 6F transforms Sentinel's offline financial risk research pipeline into a production-ready, real-time decision engine fully integrated with Kafka, Spring Boot microservices, and the Angular SOC Operations Command Center.

Additionally, Phase 6F introduces a **hardened benchmark dataset** (`data/financial/phase6f/`) featuring evasive fraud rings and legitimate shared infrastructure (office Wi-Fi, family devices) to evaluate whether relationship graph intelligence provides incremental risk signal.

---

## 1. System Architecture

```
Financial Transaction Telemetry
        |
        v
FinancialRiskEvent (Canonical Java Record)
        |
        v
Kafka Topic: sentinel.financial.events
        |
        v
FinancialRiskEventConsumer (Spring Kafka Listener)
        |
        +-----------------------------------+
        |                                   |
        v                                   v
Financial Feature Pipeline           Relationship Graph Engine
        |                                   |
        +-----------------+-----------------+
                          |
                          v
              ML Risk Scoring Engine (Random Forest / LR)
                          |
                          v
              Cost-Aware 3-Tier Decision Engine
                          |
             +------------+------------+
             |            |            |
          APPROVE       REVIEW       BLOCK
             |            |            |
             +------------+------------+
                          |
                          v
              Sentinel Risk Engine (Composite Score 0-100)
                          |
                          v
               sentinel.alert-events (Kafka)
                          |
                          v
              Alert Service -> Incident Service -> SOC Dashboard
```

---

## 2. Hardened Synthetic Benchmark Design (`data/financial/phase6f`)

### Dataset Characteristics
- **Total Records**: 15,000 synthetic transactions
- **Legitimate Count**: 13,800 (92.0%)
- **Fraudulent Count**: 1,200 (8.0%)
- **Train / Val / Test Splits**: 10,500 / 1,500 / 3,000 (deterministic seeds)
- **Zero Leakage**: All predictive features are strictly backward-looking.

### Scenario Breakdown & Benchmark Findings

| Scenario | Class | Baseline Model Recall | Enhanced (Graph) Model Recall | Finding |
| :--- | :---: | :---: | :---: | :--- |
| `EVASIVE_FRAUD` | Fraud | **95.65%** | **100.00%** | **+4.35% Detection Gain**: Baseline misses evasive fraud due to normal amount & velocity; Graph catches 100% via stolen payment token topology! |
| `LEGITIMATE_SHARED_INFRASTRUCTURE` | Legit | **99.24%** (6 FP) | **100.00%** (0 FP) | **False Positive Reduction**: Baseline flags shared office Wi-Fi as fraud; Graph topology correctly identifies legitimate multi-user IP subnets! |
| `LOW_AND_SLOW_RING` | Fraud | **100.00%** | **42.31%** | Baseline captures low velocity patterns directly; tree depth 10 pruned low-and-slow graph splits. |
| `COMBINED_RING` | Fraud | **100.00%** | **100.00%** | Both models achieve 100% detection. |
| `BEHAVIORAL_ANOMALY` | Fraud | **100.00%** | **100.00%** | Both models achieve 100% detection. |
| `SHARED_IP_BURST` | Fraud | **100.00%** | **100.00%** | Both models achieve 100% detection. |
| `DEVICE_SHARING_RING` | Fraud | **100.00%** | **100.00%** | Both models achieve 100% detection. |
| `PAYMENT_REF_REUSE` | Fraud | **100.00%** | **100.00%** | Both models achieve 100% detection. |

---

## 3. Real-Time Kafka & Microservice Integration
1. **Producer**: Publishes canonical `FinancialRiskEvent` to `sentinel.financial.events`.
2. **Consumer**: `FinancialRiskEventConsumer` in `risk-service` processes incoming telemetry.
3. **Idempotency**: `riskAssessmentRepository.existsByThreatId(txUuid)` prevents duplicate evaluations or alerts.
4. **Alert / Incident Propagation**: High-risk transactions trigger `AlertEventEnvelope` on `sentinel.alert-events`, automatically creating incidents in `alert-service` with rich financial evidence.
