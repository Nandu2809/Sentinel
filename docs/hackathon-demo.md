# Sentinel — 5-Minute Hackathon Judge Demonstration Guide

## Track 2 — AI Risk Manager
**Razorpay Hackathon**

---

## Story Baseline
> *"Traditional payment fraud engines rely on rigid static rules or simple binary classifiers. Sentinel is a real-time, relationship-aware, cost-sensitive AI Risk Management system that converts transaction telemetry and entity topology into explainable business risk decisions."*

---

## 5-Minute High-Impact Presentation Script

### 00:00 – 00:30 | The Core Problem
- **Presenter**: "Financial platforms face an asymmetric risk dilemma. Blocking legitimate transactions creates lost GMV and customer friction ($C_{\text{FP}} = \text{₹1,200}$). Approving fraud creates chargeback losses ($C_{\text{FN}} = \text{₹6,800}$). Sentinel solves this by shifting from binary fraud classification to cost-optimized AI risk decisions."
- **Visual**: Show Top Executive KPI Summary at `/financial-risk`.

### 00:30 – 01:00 | Architecture & Telemetry Loop
- **Presenter**: "When a financial transaction is initiated, it flows through Kafka (`sentinel.financial.events`) into our Financial Risk Engine. We extract 34 features—9 baseline transaction features and 25 relationship graph features."
- **Visual**: Highlight the **Real-Time Risk Timeline** at the bottom of the SOC Command Center showing the 8-stage pipeline.

### 01:00 – 02:00 | Scenario A: Legitimate Transaction (APPROVE)
- **Presenter**: "Here is a standard checkout transaction (`tx_fin_994823`). The user has an established 340-day account, a single verified device, and low velocity."
- **Visual**: Select the `APPROVE` row in the stream table.
  - Risk Score: **12 / 100**
  - Decision: **APPROVE** ($p < 0.25$)
  - Expected Cost: **₹0**
- **Presenter**: "Why did Sentinel approve? Our Explainable AI panel shows standard profile indicators with 0 ring connectivity."

### 02:00 – 03:00 | Scenario C: Coordinated Fraud Ring (BLOCK)
- **Presenter**: "Now observe what happens when a coordinated fraud ring strikes (`tx_fin_994820`). A new account attempts a ₹48,500 transaction."
- **Visual**: Click `+ EVALUATE SYNTHETIC TX` or select `BLOCK` transaction.
  - Risk Score: **87 / 100**
  - Decision: **BLOCK** ($p \ge 0.50$)
  - Expected Chargeback Avoided: **₹6,800**
- **Presenter**: "Look at the Relationship Intelligence Graph. The central user is connected to a hardware device shared across 8 accounts and an IP subnet linked to 11 accounts. Sentinel automatically detected the Shared Payment Ring."

### 03:00 – 04:00 | Policy Simulator & Risk Economics
- **Presenter**: "How do risk managers tune decision policies without risking live revenue? Sentinel includes a built-in Policy Threshold Simulator and Business Cost Curve."
- **Visual**: Adjust the BLOCK threshold slider from 0.50 to 0.70 in the Policy Simulator panel. Show live comparison between **Current Production Policy** and **Simulated Policy**.

### 04:00 – 05:00 | SOC Integration & Honest Scientific Findings
- **Presenter**: "Sentinel automatically dispatches high-risk alerts to Kafka (`sentinel.alert-events`) and creates SOC Incidents."
- **Visual**: Click **🚨 OPEN INCIDENT WORKSPACE** to navigate seamlessly to `/incidents` or click **🔍 THREAT HUNTING WORKBENCH** to view `/threat-hunting`.
- **Presenter**: "Finally, we uphold strict scientific discipline. On Phase 6F benchmarks, our relationship graph engine improved recall on evasive fraud from 95.65% to 100% and eliminated false positives on shared corporate infrastructure from 6 to 0. We also document where graph features trade off on low-and-slow rings."

---

## Key Judge Q&A Takeaways

1. **"How is Sentinel different from standard fraud detection models?"**
   - *Answer*: Sentinel evaluates multi-hop entity relationship topology (Device, IP, Payment Token, Merchant) and optimizes decision boundaries against asymmetric business costs ($C_{\text{FP}}$ vs $C_{\text{FN}}$).
2. **"Does Sentinel prevent data leakage?"**
   - *Answer*: Yes. All feature scaling was fitted exclusively on `train.csv`. Operating thresholds were tuned on `validation.csv`, and held-out test data was evaluated strictly once.
3. **"Is the system human-in-the-loop?"**
   - *Answer*: Yes. Transactions with intermediate risk probabilities ($0.25 \le p < 0.50$) are routed to `REVIEW` with explicit analyst review recommendations.
