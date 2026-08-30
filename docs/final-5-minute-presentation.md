# Sentinel — Final 5-Minute Spoken Presentation Script

## Overview
This script is written for natural spoken delivery during the 5-minute hackathon judge presentation.

---

## 🎙️ Spoken Presentation Script

### `0:00 – 0:30` — The Problem & Track 2 Opening
> *"Good morning judges. In digital payment systems, traditional fraud detection tools suffer from binary classification tunnel vision. When a user submits a checkout, legacy systems ask only one rigid question: 'Is this transaction fraud?'
> 
> They miss complex multi-account fraud rings, ignore chargeback economics, and insult legitimate customers with false blocks.
> Presenting **Sentinel**—an explainable, relationship-aware, cost-sensitive AI Risk Management Platform built for Razorpay Track 2."*

### `0:30 – 1:00` — The Sentinel Solution
> *"Sentinel changes the paradigm from simple binary classification to end-to-end risk management.
> 
> Uniting seven core capabilities—Financial Telemetry Ingestion, Relationship Graph Topology, AI Behavioral Anomaly Detection, Machine Learning Risk Scoring, Business Cost Optimization, Human-in-the-Loop Analyst Review, and SOC Incident Response—Sentinel provides complete operational clarity."*

### `1:00 – 1:30` — Platform Architecture
> *"Looking at our backend architecture: transaction envelopes (`FinancialRiskEvent`) stream into an Apache Kafka event bus on topic `sentinel.financial.events`.
> 
> Our Java 21 Spring Boot Risk Service consumes the stream, extracts 34 features—including in-memory graph topology—and coordinates with our Python AI Engine. The calculated risk score and cost decision are recorded in PostgreSQL and rendered live on our Angular 17 Command Center at `/financial-risk`."*

### `1:30 – 2:00` — Scenario A: Legitimate Transaction (`APPROVE`)
> *"Let's see this in action on our live system.
> 
> We trigger **Scenario A**—a standard ₹2,450 grocery checkout in Delhi. Sentinel extracts features, inspects node topology, and computes a Risk Score of **12 / 100** ($p = 0.12$).
> Because $p < 0.25$, Sentinel issues an instant **APPROVE**. Expected business loss is ₹0. No alerts or incidents are created, ensuring zero customer friction."*

### `2:00 – 2:30` — Scenario B: Ambiguous Transaction (`REVIEW`)
> *"Now we evaluate **Scenario B**—a ₹14,500 travel purchase from a shared corporate IP subnet.
> 
> Standard fraud rules might block this due to IP volume. Sentinel's graph engine recognizes corporate subnet sharing while noting elevated velocity, computing a Risk Score of **54 / 100** ($p = 0.38$).
> Because $0.25 \le p < 0.50$, Sentinel routes this to **REVIEW**. Look at the amber indicator: `HUMAN-IN-THE-LOOP: Analyst review recommended`. Instead of insulting a customer with an unnecessary block, Sentinel engages analyst verification at a small ₹400 cost."*

### `2:30 – 3:30` — Scenario C: High-Risk Fraud Ring (`BLOCK`)
> *"Now we trigger **Scenario C**—a coordinated fraud ring attempting a ₹48,500 checkout.
> 
> Instantly, our SVG Relationship Topology Graph turns red! Look at the node inspector: 8 user accounts sharing a single hardware device fingerprint, and 3 accounts sharing a payment card reference!
> Risk Score surges to **87 / 100** ($p = 0.87$). Sentinel issues an automated **BLOCK** ($p \ge 0.50$), preventing a ₹6,800 chargeback loss."*

### `3:30 – 4:00` — Explainable AI & Graph Signals
> *"Sentinel isn't a black box. Look at the Explainable AI panel ('Why This Decision?'): Sentinel lists the exact contributing factors—Shared Payment Ring Cluster, Device Sharing, and 1-Hour Velocity—comparing observed values against baseline percentiles."*

### `4:00 – 4:30` — Cost-Aware Policy Simulator
> *"Down here is our Risk Policy Threshold Simulator and visual SVG Business Cost Curve. Security leaders can adjust sliders for $\tau_{\text{block}}$ and $\tau_{\text{review}}$ to simulate policy impact on approval rates and financial losses in real time before mutating production rules."*

### `4:30 – 5:00` — Incident Response & Closing
> *"Blocking Scenario C automatically dispatches a critical alert event to `sentinel.alert-events` and creates an active incident in our SOC workspace (`INC-2026-1000`). With one click on '🚨 OPEN INCIDENT WORKSPACE', security analysts transition to `/incidents` to initiate mitigation.
> 
> Sentinel doesn't just predict whether a payment is fraud. It determines how risky it is, why it is risky, what relationships surround it, what the business risks by making the wrong decision, and what the security team should do next. Thank you!"*
