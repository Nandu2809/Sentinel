# Sentinel Phase 8 — Live Demo Script & Operator Manual

## Overview
This manual provides exact click actions, spoken commentary, expected visual cues, failure risks, and backup actions for the 5-minute hackathon live demonstration.

---

## 🎬 5-Minute Live Demo Execution Guide

### Segment 1: `00:00 - 00:30` — Problem & Track 2 Opening
- **What to Click**: Open browser to `http://localhost:4200/financial-risk`.
- **What to Say**: *"Good morning judges. Traditional fraud systems ask: 'Is this transaction fraud?' Sentinel asks: 'How risky is it, why is it risky, what entity relationships surround it, what will it cost if we are wrong, and what should the SOC do next?'"*
- **What Judge Should Notice**: Dark-mode, high-density Angular 17 Command Center header displaying `SYSTEM: LIVE`, `KAFKA: CONNECTED`, `GRAPH: ONLINE`.
- **Failure Risk**: Page load delay.
- **Backup Action**: Refresh page (`Ctrl+F5`) or point to pre-loaded browser tab.

---

### Segment 2: `00:30 - 01:00` — Sentinel Architecture
- **What to Click**: Point cursor to top status cards and Kafka stream banner.
- **What to Say**: *"Sentinel ingests transaction envelopes via Kafka into Java 21 Spring Boot microservices. Before machine learning, Sentinel extracts 34 features including in-memory graph topology, optimizing decisions against asymmetric business costs."*
- **What Judge Should Notice**: Real-time KPI modules showing 100% telemetry, approved/review/block counters, and expected loss stats.
- **Failure Risk**: Status card data empty.
- **Backup Action**: Click top `↻ REFRESH` button.

---

### Segment 3: `01:00 - 01:40` — Scenario A (Legitimate $\rightarrow$ `APPROVE`)
- **What to Click**: Click `SCENARIO A: LEGITIMATE (APPROVE)` button in quick-tester bar.
- **What to Say**: *"Scenario A: a standard ₹2,450 grocery checkout in Delhi. Risk Score is 12 / 100 ($p = 0.12$). Sentinel issues an instant APPROVE with ₹0 expected loss. Zero customer friction."*
- **What Judge Should Notice**: Live stream table prepends green `tx_legit_10291` row; graph shows single isolated user node.
- **Failure Risk**: Button click fails to prepend row.
- **Backup Action**: Click top `↻ REFRESH` or select existing green row in stream table.

---

### Segment 4: `01:40 - 02:20` — Scenario B (Ambiguous $\rightarrow$ `REVIEW`)
- **What to Click**: Click `SCENARIO B: AMBIGUOUS (REVIEW)` button.
- **What to Say**: *"Scenario B: a ₹14,500 travel purchase on a corporate NAT IP. Risk Score is 54 / 100 ($p = 0.38$). Sentinel routes this to REVIEW. Look at the amber indicator: HUMAN-IN-THE-LOOP analyst review recommended. We avoid insulting a customer with an unnecessary block."*
- **What Judge Should Notice**: Amber `REVIEW` badge appears; `HUMAN-IN-THE-LOOP` analyst review indicator banner activates.
- **Failure Risk**: Amber banner does not display.
- **Backup Action**: Click on row `tx_ambig_44120` in stream table to force selection.

---

### Segment 5: `02:20 - 03:20` — Scenario C (Fraud Ring $\rightarrow$ `BLOCK`)
- **What to Click**: Click `SCENARIO C: HIGH-RISK RING (BLOCK)` button.
- **What to Say**: *"Scenario C: a ₹48,500 fraud ring attack. Risk Score surges to 87 / 100 ($p = 0.87$). Sentinel issues an automated BLOCK. Look at the red SVG topology graph: 8 user accounts sharing a single hardware device fingerprint, and 3 accounts sharing a card token reference!"*
- **What Judge Should Notice**: SVG relationship graph turns glowing red; multi-hop node connections animate; red `BLOCK` badge rendered.
- **Failure Risk**: Graph SVG fails to update.
- **Backup Action**: Click directly on red node in graph component to trigger inspector.

---

### Segment 6: `03:20 - 04:00` — Explainable AI & Graph Signals
- **What to Click**: Point to `EXPLAINABLE AI — WHY THIS DECISION?` panel on left.
- **What to Say**: *"Sentinel isn't a black box. Look at the attributions: Shared Payment Ring Cluster, Hardware Fingerprint Sharing (8 accounts), IP Subnet Concentration (11 accounts), and 1-Hour Velocity."*
- **What Judge Should Notice**: 4 formatted risk factor cards with observed values and explanation text.
- **Failure Risk**: Attribution list empty.
- **Backup Action**: Re-select `tx_ring_88291` in stream table.

---

### Segment 7: `04:00 - 04:30` — Risk Policy Simulator & Business Cost Curve
- **What to Click**: Move `Simulated BLOCK Threshold (τ_block)` slider to `0.70`.
- **What to Say**: *"Down here is our Risk Policy Threshold Simulator and visual SVG Business Cost Curve. Security leaders can adjust sliders to simulate how changing policy thresholds impacts approval rates and expected chargeback loss in real time."*
- **What Judge Should Notice**: Simulated approval and cost metrics update dynamically; SVG curve highlights minimum cost at $\tau^* = 0.50$.
- **Failure Risk**: Slider input doesn't update metrics.
- **Backup Action**: Explain static curve parameters ($C_{\text{FP}} = \text{₹1,200}$, $C_{\text{FN}} = \text{₹6,800}$).

---

### Segment 8: `04:30 - 05:00` — SOC Incident Bridge & Closing Pitch
- **What to Click**: Click `🚨 OPEN INCIDENT WORKSPACE (/incidents)` in investigation modal drawer.
- **What to Say**: *"Blocking Scenario C automatically dispatches a critical alert to Kafka and opens SOC Incident INC-2026-1000. With one click, analysts transition to /incidents to initiate mitigation. Sentinel doesn't just classify risk—it manages it end-to-end. Thank you!"*
- **What Judge Should Notice**: Router seamlessly navigates to `/incidents` workspace displaying active incident record.
- **Failure Risk**: Navigation fails.
- **Backup Action**: Click `/incidents` link manually in left navigation rail.
