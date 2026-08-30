# Sentinel — Final 5-Minute Hackathon Demo Script

## Overview
This document provides the exact minute-by-minute judge presentation script for **Razorpay Hackathon Track 2 — AI Risk Manager**.

---

## ⏱️ Timeline & Presentation Flow

```
[00:00 - 00:30]  Problem Definition & Track 2 Positioning
       │
[00:30 - 01:15]  Sentinel Architecture & Relationship Graph Concept
       │
[01:15 - 02:00]  Scenario A: Legitimate Transaction → APPROVE
       │
[02:00 - 02:45]  Scenario B: Ambiguous Transaction → REVIEW (Human-in-the-Loop)
       │
[02:45 - 03:45]  Scenario C: High-Risk Fraud Ring → BLOCK & Graph Topology
       │
[03:45 - 04:30]  Explainable AI, Policy Simulator & Expected Business Cost Curve
       │
[04:30 - 05:00]  Alert → Incident Bridge → Resolution & Closing Pitch
```

---

## 🎙️ Minute-by-Minute Script

### `00:00 - 00:30` — The Problem & Track 2 Positioning
> *"Good morning judges. Traditional fraud detection systems suffer from two major flaws: binary classification tunnel vision and isolated transaction processing. When a user submits a checkout, legacy systems ask only: 'Is this transaction fraud?' They miss complex relationship rings, chargeback economics, and false positive customer friction.
>
> Presenting **Sentinel** — an explainable, relationship-aware, cost-sensitive AI Risk Management Platform built for Track 2."*

### `00:30 - 01:15` — Sentinel Architecture
> *"Sentinel ingests transaction envelopes via Kafka topics (`sentinel.financial.events`) into a high-throughput Java 21 Spring Boot risk pipeline. Before applying machine learning, Sentinel constructs an in-memory relationship topology graph connecting Users, Hardware Device Fingerprints, IP Subnets, Payment Token References, and Merchants across 34 extracted features.
>
> Sentinel evaluates risk using asymmetric business costs: false positive blocks ($C_{\text{FP}} = \text{₹1,200}$), false negative chargebacks ($C_{\text{FN}} = \text{₹6,800}$), analyst reviews ($C_{\text{REVIEW}} = \text{₹400}$), and friction ($C_{\text{FRICTION}} = \text{₹160}$)."*

### `01:15 - 02:00` — Scenario A: Normal Transaction (`APPROVE`)
> *"Let's look at live execution on our Command Center dashboard at `/financial-risk`.
>
> We trigger **Scenario A** — a standard user buying groceries in Delhi for ₹2,450. Sentinel extracts features, inspects the graph, and computes a Risk Score of **12 / 100** ($p = 0.12$).
> Because $p < 0.25$, Sentinel issues an instant **APPROVE**. Expected business loss is ₹0. No alerts or incidents are created, ensuring zero customer friction."*

### `02:00 - 02:45` — Scenario B: Ambiguous Transaction (`REVIEW`)
> *"Now let's test **Scenario B** — a user making a ₹14,500 travel purchase from a shared corporate IP subnet.
>
> Baseline models might mistakenly flag this due to shared IP volume. Sentinel's graph engine recognizes corporate subnet sharing while noting elevated velocity. Risk Score is computed at **54 / 100** ($p = 0.38$).
> Because $0.25 \le p < 0.50$, Sentinel routes this to **REVIEW**. An amber banner appears: `HUMAN-IN-THE-LOOP: Analyst review recommended`. Instead of alienating a legitimate customer with a block, Sentinel engages analyst verification at ₹400 cost."*

### `02:45 - 03:45` — Scenario C: High-Risk Fraud Ring (`BLOCK`)
> *"Now we trigger **Scenario C** — a high-risk coordinated fraud ring attempting a ₹48,500 digital checkout.
>
> Instantly, Sentinel's SVG Relationship Topology Graph lights up in red! Look at the node inspector: this single device fingerprint is linked to 8 different user accounts, and the payment card reference is shared across 3 unlinked accounts.
> Risk Score surges to **87 / 100** ($p = 0.87$). Sentinel issues an automated **BLOCK** ($p \ge 0.50$), avoiding a ₹6,800 chargeback loss."*

### `03:45 - 04:30` — Explainable AI, Policy Simulator & Business Cost Curve
> *"Sentinel does not operate as a black box. Look at the **Explainable AI Panel** ('Why This Decision?'): Sentinel lists the exact contributing factors — Shared Payment Ring Cluster, Device Sharing, and 1-Hour Velocity.
>
> Down here is our **Risk Policy Threshold Simulator** and **Expected Business Cost Curve**. Security leaders can adjust sliders for $\tau_{\text{block}}$ and $\tau_{\text{review}}$ to simulate policy impact on approval rates and financial losses in real-time."*

### `04:30 - 05:00` — Incident Bridge & Closing Pitch
> *"Upon blocking Scenario C, Sentinel automatically dispatches a critical alert event to `sentinel.alert-events` and creates an active incident in our SOC workspace (`INC-2026-1000`). With one click on '🚨 OPEN INCIDENT WORKSPACE', security analysts transition to `/incidents` to inspect audit logs and trigger mitigation controls.
>
> Sentinel does not simply ask: 'Is this transaction fraud?' It tells you how risky it is, why it's risky, what it will cost if you're wrong, and how to resolve it. Thank you!"*
