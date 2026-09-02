# Sentinel Phase 8 — Value Proposition Document

## Executive Summary
Sentinel addresses the fundamental challenge of modern digital payment security: **Evaluating financial risk when fraud spans distributed entity relationships rather than single transactions.**

---

## 💎 The Four Pillars of Value

### 1. TECHNICAL VALUE: Relationship-Aware Entity Graph Intelligence
- **Multi-Hop Graph Topology**: Traverses 1-hop, 2-hop, and 3-hop relationships connecting Users, Hardware Devices, IP Subnets, Payment Token References, and Merchants.
- **Evasive Fraud Protection**: Detects low-velocity fraud rings rotating across 8+ accounts sharing hardware or payment handles.
- **NAT / Shared Subnet Precision**: Preserves high precision for legitimate users sharing corporate IP subnets or university Wi-Fi ranges.

### 2. AI VALUE: Multi-Dimensional Behavioral & Graph Feature Fusion
- **34-Feature Extraction**: Fuses 9 baseline transaction features with 25 graph topology features.
- **Non-Linear Inference**: Employs Random Forest ensembles to output calibrated risk probability scores $p \in [0.0, 1.0]$.
- **Explainable Attribution**: Returns explicit risk factor reasons (`HIGH_DEVICE_SHARING`, `SHARED_IP_BURST`, `PAYMENT_REF_REUSE`) alongside graph node statistics for every evaluation.

### 3. BUSINESS VALUE: Asymmetric Loss & Cost Policy Optimization
- **Monetary Loss Optimization**: Decision operating boundaries ($\tau_{\text{block}}=0.50$, $\tau_{\text{review}}=0.25$) minimize total expected business loss:
  $$\mathbb{E}[\text{Cost}] = C_{\text{FP}}N_{\text{FP}} + C_{\text{FN}}N_{\text{FN}} + C_{\text{REVIEW}}N_{\text{REVIEW}} + C_{\text{FRICTION}}N_{\text{FRICTION}}$$
- **Direct Margin Protection**: Balances chargeback loss penalties ($C_{\text{FN}}=\text{₹6,800}$) against customer drop-off friction ($C_{\text{FP}}=\text{₹1,200}$) and analyst review labor ($C_{\text{REVIEW}}=\text{₹400}$).
- **Human-in-the-Loop Tiering**: Routes ambiguous transactions ($0.25 \le p < 0.50$) to manual review rather than inflicting blunt automated blocks.

### 4. OPERATIONAL VALUE: Direct Bridge from Risk Detection to SOC Operations
- **Asynchronous Event Pipeline**: High-risk decisions publish `AlertEvent` to Kafka (`sentinel.alert-events`) without blocking payment gateway response threads.
- **Automated Incident Escalation**: Auto-generates structured Incidents (`INC-2026-XXXX`) with timeline evidence.
- **Interactive SOC Workstation**: Empowers security analysts with real-time graph visualizations (`/financial-risk`), incident investigation management (`/incidents`), and threat hunting pattern queries (`/threat-hunting`).
