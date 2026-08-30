# Sentinel — Final Product Story

## The Core Concept

> *"Sentinel is an AI-powered financial risk management platform that doesn't merely classify transactions as fraud or legitimate.
> 
> It understands transaction behavior, relationships, AI signals, business cost, and operational response before deciding whether to **APPROVE**, **REVIEW**, or **BLOCK**."*

---

## 📖 The Narrative Arc

```
1. THE PROBLEM
Chargeback Losses vs False Positive Friction
       │
       ▼
2. EXISTING LIMITATIONS
Binary Fraud Models & Static Rule Engines
       │
       ▼
3. THE SENTINEL SOLUTION
Integrated AI Financial Risk Manager
       │
       ▼
4. RELATIONSHIP INTELLIGENCE
In-Memory Graph Topology Ring Detection
       │
       ▼
5. COST-AWARE DECISIONING
3-Tier Policy Thresholds (APPROVE / REVIEW / BLOCK)
       │
       ▼
6. HUMAN-IN-THE-LOOP & SOC RESPONSE
Automated Incident Creation & Threat Hunting
```

---

## 🎙️ The Spoken Storyline

### The Problem & Existing Limitation
In digital payments, legacy fraud detection tools ask only one rigid question: *"Is this transaction fraud?"*
Static rule engines flag innocent users buying travel on corporate subnets, generating false blocks ($C_{\text{FP}} = \text{₹1,200}$). At the same time, simple binary ML models miss coordinated fraud rings operating across multiple accounts, leading to massive chargeback penalties ($C_{\text{FN}} = \text{₹6,800}$).

### The Sentinel Difference
Sentinel changes the paradigm from simple binary classification to **operational risk management**.
As transaction envelopes (`FinancialRiskEvent`) stream through Apache Kafka, Sentinel constructs an in-memory relationship graph connecting Users, Device Fingerprints, IP Subnets, Payment Token References, and Merchants across 34 extracted features.

### Cost-Aware Decisions & Human Review
Sentinel evaluates transactions using asymmetric business costs:
- **`APPROVE`** ($p < 0.25$): Instant pass with zero customer friction.
- **`REVIEW`** ($0.25 \le p < 0.50$): Routes ambiguous events to a human-in-the-loop analyst review queue ($C_{\text{REVIEW}} = \text{₹400}$), avoiding false block churn.
- **`BLOCK`** ($p \ge 0.50$): Mitigates high-risk fraud rings, automatically emitting alerts to `sentinel.alert-events` and creating active SOC incidents (`INC-2026-1000`).

### The Result
Sentinel provides end-to-end operational clarity: it tells security teams how risky a transaction is, why it is risky, what entity relationships surround it, what the business risks by making the wrong decision, and what the SOC should do next.
