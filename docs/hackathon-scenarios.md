# Sentinel Phase 7 — Hackathon Demonstration Scenarios

## Canonical End-to-End Demo Path
The canonical flow demonstrates Sentinel's real-time financial risk pipeline from transaction event ingestion to SOC incident resolution:

```
Synthetic Financial Transaction
        ↓
FinancialRiskEvent Envelope
        ↓
Kafka: sentinel.financial.events
        ↓
FinancialRiskEventConsumer (risk-service)
        ↓
34 Financial + Relationship Topology Features
        ↓
Baseline / Enhanced ML Engine
        ↓
Cost-Aware Decision Engine (APPROVE < 0.25 <= REVIEW < 0.50 <= BLOCK)
        ↓
Kafka: sentinel.alert-events (If HIGH / CRITICAL)
        ↓
Alert Service & Incident Service
        ↓
Sentinel SOC Command Center (/financial-risk)
        ↓
Analyst Investigation → Incident Resolution (/incidents)
```

---

## 3 Demonstration Scenarios

### SCENARIO A — LEGITIMATE TRANSACTION (`APPROVE`)

- **Input Telemetry**: `tx_legit_10291` | User: `usr_legit_301` | Amount: ₹2,450 | Merchant: `mch_razorpay_food` | Location: Delhi, IN
- **Features Extracted**:
  - Account Age: 340 days
  - 1-Hour Velocity: 1 tx/hr
  - Hardware Fingerprint Sharing: 1 Account
  - IP Subnet Sharing: 1 Account
- **Graph Topology Signals**: Single isolated user node $\rightarrow$ Unique device $\rightarrow$ Unique IP $\rightarrow$ Merchant. No ring edges.
- **Model Output & Risk Score**: Risk Score = `12 / 100` ($p = 0.12$).
- **Cost-Aware Decision**: `APPROVE` ($p < 0.25$). Expected Business Loss = **₹0**.
- **Alert & Incident Behavior**: No alert dispatched to Kafka, no SOC incident created.
- **Frontend Visualization**: Displays green `APPROVE` badge in live stream, single-node graph, 0 risk signals in Explainable AI panel.

---

### SCENARIO B — AMBIGUOUS TRANSACTION (`REVIEW`)

- **Input Telemetry**: `tx_ambig_44120` | User: `usr_corp_301` | Amount: ₹14,500 | Merchant: `mch_razorpay_travel` | Location: Bengaluru, IN
- **Features Extracted**:
  - Account Age: 45 days
  - 1-Hour Velocity: 4 tx/hr
  - Hardware Fingerprint Sharing: 1 Account
  - IP Subnet Sharing: 6 Accounts (Corporate NAT)
- **Graph Topology Signals**: Multi-account IP sharing (6 accounts on corporate gateway), velocity elevated above baseline.
- **Model Output & Risk Score**: Risk Score = `54 / 100` ($p = 0.54$ raw, adjusted to $p = 0.38$ after corporate NAT graph feature disambiguation).
- **Cost-Aware Decision**: `REVIEW` ($0.25 \le p < 0.50$). Expected Analyst Investigation Cost = **₹400**.
- **Human-in-the-Loop Workflow**: Visible banner: `HUMAN-IN-THE-LOOP: Analyst review recommended (Intermediate confidence)`.
- **Alert & Incident Behavior**: Warning alert logged to audit console feed.
- **Frontend Visualization**: Displays amber `REVIEW` badge, highlighted IP node in SVG graph, corporate subnet attribution in Explainable AI panel.

---

### SCENARIO C — HIGH-RISK FRAUD RING (`BLOCK`)

- **Input Telemetry**: `tx_ring_88291` | User: `usr_ring_8841` | Amount: ₹48,500 | Merchant: `mch_razorpay_digital` | Location: Mumbai, IN
- **Features Extracted**:
  - Account Age: 2 days
  - 1-Hour Velocity: 9 tx/hr
  - Hardware Fingerprint Sharing: 8 Accounts
  - IP Subnet Sharing: 11 Accounts
  - Payment Token Reuse: Card Token shared across 3 unlinked accounts
- **Graph Topology Signals**: Multi-hop fraud ring detected! Shared hardware device fingerprint (8 accounts), shared card reference (3 accounts), high transaction velocity.
- **Model Output & Risk Score**: Risk Score = `87 / 100` ($p = 0.87$).
- **Cost-Aware Decision**: `BLOCK` ($p \ge 0.50$). Avoided Fraud Chargeback Loss = **₹6,800**.
- **Alert & Incident Behavior**: Critical alert emitted to `sentinel.alert-events`, automated SOC incident `INC-2026-1000` created.
- **Frontend Visualization**: Red `BLOCK` badge, glowing red multi-hop topology graph, 4 critical risk factors listed in Explainable AI, direct button to `/incidents` workspace.
