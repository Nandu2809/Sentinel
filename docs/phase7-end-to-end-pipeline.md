# Sentinel Phase 7 — End-to-End Pipeline & Relationship Graph Verification

**Generated:** 2026-09-02  
**Track 2 Focus:** Coordinated Financial Risk Management & SOC Pipeline

---

## 1. End-to-End Event Architecture

The Sentinel platform implements an end-to-end event-driven workflow for continuous financial risk intelligence:

```
Financial Transaction 
       │
       ▼
FinancialRiskEvent (Canonical JSON Envelope)
       │
       ▼
Kafka Topic: sentinel.financial.events
       │
       ▼
Feature Extraction Engine (34 Enhanced Baseline + Graph Topology Features)
       │
       ▼
Relationship Graph Topology Builder (Entity Co-occurrence Graph)
       │
       ▼
ML Risk Evaluation Engine (Gradient Boosted Ensemble / Isolation Forest)
       │
       ▼
Risk Score Generation (0 - 100 Scale)
       │
       ▼
Cost-Aware Decision Policy (APPROVE / REVIEW / BLOCK)
       │
       ▼
Alert Management Service (sentinel.alert-events)
       │
       ▼
SOC Incident Creation & Timeline Assignment
       │
       ▼
Threat Hunting Indexing & Bridge
       │
       ▼
Mailpit Security Email Notification (Port 8025)
```

---

## 2. Verified Scenario Execution Trace

### Scenario A — Legitimate Transaction
- **Input Parameters:** Amount: ₹2,450 | Account Age: 340 days | Hourly Velocity: 1 | Shared Devices: 1 | Shared IPs: 1
- **ML Output:** Risk Score: **10** (LOW)
- **Cost Policy Decision:** **APPROVE**
- **Pipeline Action:** Instant transaction pass. No alert or incident created.

### Scenario B — Ambiguous Transaction (Human-In-The-Loop)
- **Input Parameters:** Amount: ₹14,500 | Account Age: 45 days | Hourly Velocity: 4 | Shared Devices: 1 | Shared IPs: 6
- **ML Output:** Risk Score: **45–54** (MEDIUM)
- **Cost Policy Decision:** **REVIEW**
- **Pipeline Action:** Flagged for Human-in-the-loop analyst review. Assigned to analyst investigation queue. No destructive automatic block.

### Scenario C — Coordinated Fraud Ring
- **Input Parameters:** Amount: ₹48,500 | Account Age: 2 days | Hourly Velocity: 9 | Shared Devices: 8 | Shared IPs: 11
- **ML Output:** Risk Score: **100** (CRITICAL)
- **Cost Policy Decision:** **BLOCK**
- **Pipeline Action:** Automated block decision emitted to `sentinel.risk-events` -> Security alert dispatched to `sentinel.alert-events` -> SOC Incident generated in PostgreSQL -> Analyst investigation timeline logged -> Security email dispatched to Mailpit.

---

## 3. Relationship Graph Signals & Graph Intelligence

Sentinel constructs real-time entity graph topologies around every financial event:

- **Entity Nodes:** User ID, Device Fingerprint, IP Subnet, Payment Reference, Merchant ID.
- **Edge Weighting:** Link density, degree centrality, shared credential counts.
- **Graph Risk Signals:**
  - `SHARED_DEVICE_CLUSTER`: Shared across 8+ user accounts within 24 hours.
  - `PAYMENT_RING_REF`: Payment instrument reference reused across multi-location accounts.
  - `SUBNET_CONCENTRATION`: High velocity ip_subnet burst.

---

## 4. Explainable AI ("Why This Decision?")

For every transaction evaluated, Sentinel exposes clear explainability signals:
- **Observed Risk Score:** 0-100 quantitative risk.
- **Top Risk Factors:** Primary contributors (e.g., *Shared Payment Ring Reference*, *Device linked to 8 accounts*).
- **Business Cost Reasoning:** Calculated expected business loss of False Positive (wrongful friction) vs. False Negative (fraud loss).
