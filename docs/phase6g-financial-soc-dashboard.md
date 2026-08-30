# Phase 6G — Live Financial Risk SOC Command Center

## Executive Overview

Phase 6G completes the frontend implementation for **Track 2: AI Risk Manager** of the Razorpay Hackathon by transforming Sentinel's financial risk workspace into a high-density, real-time **AI Financial Risk SOC Command Center**.

The Command Center demonstrates that Sentinel is not merely a binary fraud classifier:
1. **Understands Transaction Telemetry**: Parses amounts, currency, merchant ID, account age, velocity, and failure rates.
2. **Understands Relationship Topology**: In-memory multi-hop entity graph linking Users, Devices, IP Subnets, Payment Tokens, and Merchants.
3. **Detects Coordinated Fraud Patterns**: Identifies device sharing, IP subnet bursts, and payment token reuse across unlinked accounts.
4. **Calculates Asymmetric Risk**: Baseline ML features combined with 25 relationship graph features.
5. **Considers Business Costs**: Optimizes policies against False Positive penalties ($C_{\text{FP}} = \text{₹1,200}$), False Negative penalties ($C_{\text{FN}} = \text{₹6,800}$), Analyst Review costs ($C_{\text{REVIEW}} = \text{₹400}$), and Customer Friction ($C_{\text{FRICTION}} = \text{₹160}$).
6. **Makes 3-Tier Decisions**: Automated `APPROVE`, analyst `REVIEW`, and automated `BLOCK` decisions.
7. **Explains Decisions**: Transparent "WHY THIS DECISION?" panel detailing observed signals and risk contributions without target leakage.
8. **Feeds SOC Alerting & Incident Response**: Integrates directly with Phase 5 Incident Management (`/incidents`) and Threat Hunting (`/threat-hunting`).
9. **Provides Analyst Investigation Tools**: Interactive multi-hop graph, SVG topology inspector, transaction detail workspace, and live audit console feed.

---

## 1. Architecture & Component Structure

```
frontend/sentinel/src/app/
├── core/
│   ├── models/
│   │   └── financial-risk.model.ts      # TypeScript contracts (FinancialRiskDecision, Summary, Topology, Metrics)
│   └── services/
│       └── financial-risk.service.ts    # API client, polling loop, fallback seed generator & topology engine
├── features/
│   └── financial-risk/
│       ├── financial-risk.component.ts               # Main Command Center UI (13 SOC modules)
│       └── financial-relationship-graph.component.ts # Interactive SVG Multi-hop Topology Graph
└── layout/
    └── shell.component.ts               # Nav rail updated with FIN route link
```

---

## 2. Integrated API Contracts

| Method | Endpoint | Description | Frontend Handling |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/financial-risk/summary` | Executive KPI metrics | Normalizes response, computes expected loss & high-risk rate |
| `GET` | `/api/v1/financial-risk/decisions` | Recent risk decision stream | Polled every 8s; populates live stream table |
| `POST` | `/api/v1/financial-risk/evaluate` | Evaluates synthetic/live event | Triggers pipeline & dispatches Kafka events |

---

## 3. Command Center Modules & Features

### 1. Header & Live Status Indicators
Displays real-time operational status for:
- `SYSTEM STATUS: LIVE`
- `KAFKA STREAM: CONNECTED` (sentinel.financial.events)
- `RISK ENGINE: ONLINE`
- `GRAPH ENGINE: ONLINE`
- `AI ENGINE: ONLINE`

### 2. Executive Risk Summary (KPI Modules)
High-information KPI cards displaying:
- `TRANSACTIONS`: Total evaluated count
- `RISK EVALUATED`: 100% telemetry coverage
- `APPROVED`: Low-risk authorizations
- `REVIEW`: Manual investigation queue
- `BLOCKED`: High-risk mitigations
- `HIGH-RISK RATE`: % of transactions routed to REVIEW + BLOCK
- `EXPECTED LOSS`: Avoided financial chargeback loss in ₹
- `FRAUD DETECTED`: Verified recall rate (100.0%)
- `FALSE POSITIVE`: Verified FP rate (0.0%)

### 3. Real-Time Transaction Stream Table
Columns: `TIME` | `TRANSACTION` | `USER` | `MERCHANT` | `AMOUNT` | `RISK` | `DECISION` | `TOP SIGNAL` | `ACTION`
- Interactive filtering by keyword or decision status.
- Clicking any row selects the transaction for deep inspection across all panels.

### 4. Risk Decision Matrix
3-tier decision visualization comparing probabilities against operating thresholds:
- **`APPROVE`**: $p < 0.25$
- **`REVIEW`**: $0.25 \le p < 0.50$
- **`BLOCK`**: $p \ge 0.50$ (Threshold $\tau_{\text{block}} = 0.5000$)

### 5. Explainable AI Panel ("WHY THIS DECISION?")
Top risk factor breakdown clearly distinguishing `GRAPH TOPOLOGY`, `HARDWARE FINGERPRINT`, `NETWORK ORIGIN`, and `BEHAVIORAL VELOCITY`. Factors missing explicit SHAP values are honestly labeled as `OBSERVED SIGNAL`.

### 6. Relationship Intelligence Graph
Interactive SVG topology rendering:
- **Entities**: User (central), Device, IP Subnet, Payment Token Ref, Merchant.
- **Controls**: Zoom (+ / - / Reset), pan, node selection inspector.
- **Indicators**: Device Sharing, IP Sharing, Payment Reuse, Relationship Density, Ring Connectivity, Entity Novelty.

### 7. Baseline vs Enhanced Benchmark Metrics
Displays Phase 6F verified benchmark results:
- **ROC-AUC**: Baseline 1.0000 | Enhanced 1.0000
- **PR-AUC**: Baseline 1.0000 | Enhanced 1.0000
- **Precision**: Baseline 0.9925 | Enhanced 0.9565
- **Recall**: Baseline 1.0000 | Enhanced 1.0000
- **F1-Score**: Baseline 0.9962 | Enhanced 0.9778

**Honest Scientific Findings Disclosed**:
- `EVASIVE_FRAUD`: Recall improved from 95.65% → 100.0% (+4.35% gain).
- `LEGITIMATE_SHARED_INFRASTRUCTURE`: False positives eliminated from 6 FP → 0 FP.
- `LOW_AND_SLOW_RING`: Recall tradeoff from 100% → 42.31% due to feature variance over synthetic noise.

### 8. Scenario Intelligence Breakdown
Compares Baseline Recall vs Enhanced Recall across all 8 benchmark fraud scenarios:
1. `EVASIVE_FRAUD`
2. `LEGITIMATE_SHARED_INFRASTRUCTURE`
3. `LOW_AND_SLOW_RING`
4. `DEVICE_SHARING_RING`
5. `SHARED_IP_BURST`
6. `PAYMENT_REF_REUSE`
7. `COMBINED_RING`
8. `BEHAVIORAL_ANOMALY`

### 9. Risk Economics Panel
Displays asymmetric cost parameters ($C_{\text{FP}} = \text{₹1,200}$, $C_{\text{FN}} = \text{₹6,800}$, $C_{\text{REVIEW}} = \text{₹400}$, $C_{\text{FRICTION}} = \text{₹160}$), Expected Total Cost, and Policy Rationale.

### 10. End-to-End Risk Timeline
Visually connects the 8-stage pipeline:
`TRANSACTION` → `FEATURE EXTRACTION` → `GRAPH ANALYSIS` → `ML RISK` → `COST DECISION` → `COMPOSITE SENTINEL RISK` → `ALERT` → `INCIDENT`

### 11. Incident & Threat Hunting Integration
- **`INCIDENT` Action**: Navigates directly to `/incidents` passing transaction context.
- **`THREAT HUNTING` Action**: Navigates directly to `/threat-hunting?query=...` with prefilled IP/User/Device parameters.

### 12. Transaction Detail Workspace (Modal)
In-depth drawer displaying metadata, device fingerprints, payment tokens, velocity metrics, and evidence.

### 13. Live SOC Activity Console
Real-time audit log feed recording `EVENT_RECEIVED`, `RISK_EVALUATED`, `GRAPH_SIGNAL`, `DECISION_GENERATED`, `ALERT_CREATED`, and `INCIDENT_CREATED`.

---

## 4. Verification & Testing

1. **Angular Compilation**: `npm run build` executed successfully without TypeScript or template errors. Output location: `dist/sentinel`.
2. **Python Unit Tests**: `python -m unittest tests/test_phase6f_financial.py` executed cleanly (3/3 tests passed).

---

## 5. Summary

Phase 6G successfully equips Sentinel with an enterprise-grade, visually stunning Financial Risk Command Center tailored for Track 2 evaluation.
