# Sentinel Phase 7 — Competitive Positioning & Technical Comparison

This document positions Sentinel conceptually against traditional fraud management approaches in the enterprise financial security market.

---

## 1. Architectural & Capability Comparison Matrix

| Capability / Approach | Rule-Based Fraud Systems | Traditional ML Fraud Models | Pure Anomaly Detection | Standalone Graph Systems | **Sentinel (Track 2 AI Risk Manager)** |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Transaction Feature Scoring** | Static hardcoded rules | Tabular ML (Random Forest) | Unsupervised Outliers | Limited tabular context | **34 Enhanced Features + Real-time Telemetry** |
| **Relationship Topology Graph** | None | None | None | Graph DB queries only | **Real-time 5-Node Graph (User/Device/IP/Payment/Merchant)** |
| **Explainable AI (Why This Decision?)** | Rule name only | Feature importances (global) | Distance metric only | Graph path string | **Per-transaction Risk Factor Attribution & Graph Signals** |
| **Cost-Aware Risk Thresholding** | Fixed threshold | Static probability cutoffs | Standard deviation cutoff | Fixed cutoff | **Dynamic Expected Business Loss Minimization ($C_{\text{FP}}, C_{\text{FN}}, C_{\text{friction}}$)** |
| **Human-In-The-Loop Workflow** | Manual Excel exports | Generic ticketing | Manual review | Graph visualizer only | **Native `/incidents` Workspace & Analyst Queue** |
| **SOC & Threat Hunting Integration** | Separate SIEM | None | None | None | **Integrated Threat Hunting Bridge (`/threat-hunting`)** |
| **Real-time Event Streaming** | Batch / Periodic | Request-response API | Batch | Graph update query | **Kafka Event Streaming Architecture** |

---

## 2. Sentinel's Differentiating Value Proposition

Sentinel differentiates itself by combining six core architectural layers into a single enterprise platform:

1. **Transaction Telemetry:** Canonical, tokenized transaction event processing with zero raw credential leakage.
2. **Relationship Graph Intelligence:** Multidimensional co-occurrence tracking across users, devices, IP subnets, and payment reference tokens.
3. **AI Behavioral Signals:** Supervised and unsupervised anomaly detection trained on hardened payment evasion patterns.
4. **Cost-Aware Policy Simulator:** Decision threshold optimization based on business loss economics rather than arbitrary mathematical cutoffs.
5. **Explainability Layer:** Clear quantitative risk factor attribution for every evaluated transaction.
6. **SOC Incident Response & Threat Hunting:** Integrated incident management, timeline auditing, and security threat hunting bridges.
