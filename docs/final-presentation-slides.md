# Sentinel — Final Presentation Slide Structure (10 Slides)

## Overview
This document outlines the 10 recommended slides for the hackathon final presentation deck.

---

## 🎨 Slide Deck Outline

### SLIDE 1: Title & Track Alignment
- **Title**: **SENTINEL — AI Risk Manager & Security Intelligence Platform**
- **Key Visual**: Sentinel logo, dark-mode terminal graphic, Track 2 badge.
- **Bullets**:
  - Razorpay Hackathon Track 2 ("AI Risk Manager")
  - Real-Time Financial Telemetry & Relationship Topology Graph
  - Asymmetric Business Cost Decision Engine & SOC Incident Response
- **What to Say**: *"Good morning. We are presenting Sentinel—an explainable, relationship-aware, cost-sensitive AI Risk Management platform built for Razorpay Track 2."* (20s)

---

### SLIDE 2: The Problem in Payment Security
- **Title**: **The Dual Dilemma: Chargeback Losses vs False Block Friction**
- **Key Visual**: Split graphic showing Chargeback Loss (₹6,800) vs False Block Churn (₹1,200).
- **Bullets**:
  - Undetected fraud rings cause devastating chargeback penalties ($C_{\text{FN}} = \text{₹6,800}$).
  - Over-aggressive fraud rules block legitimate checkouts ($C_{\text{FP}} = \text{₹1,200}$).
  - Legacy models ask binary questions: *"Is this fraud?"* without context or cost awareness.
- **What to Say**: *"Payment platforms face two massive threats: missed fraud causes chargebacks, while false blocks insult legitimate customers. Binary classifiers provide zero cost awareness or operational workflow."* (25s)

---

### SLIDE 3: The Sentinel Solution
- **Title**: **Beyond Classification: End-to-End AI Risk Management**
- **Key Visual**: Integrated pipeline graphic (Telemetry $\rightarrow$ Graph $\rightarrow$ ML $\rightarrow$ Cost $\rightarrow$ SOC).
- **Bullets**:
  - **Telemetry Ingestion**: Real-time Kafka streaming (`sentinel.financial.events`).
  - **Relationship Intelligence**: In-memory topology graph across 34 extracted features.
  - **Cost Optimization**: 3-tier policy model (`APPROVE`, `REVIEW`, `BLOCK`).
  - **Operational Bridge**: Automated SOC incident response (`INC-2026-1000`).
- **What to Say**: *"Sentinel doesn't just classify transactions. It ingests telemetry via Kafka, extracts relationship graph features, optimizes decisions against business loss, and bridges threats to SOC incident response."* (25s)

---

### SLIDE 4: Platform Architecture
- **Title**: **Cloud-Native Microservices & Streaming Event Bus**
- **Key Visual**: Clean microservices block diagram (Gateway, Auth, Threat, Risk, Alert, Report, AI Engine, Kafka, Postgres).
- **Bullets**:
  - 8 Java 21 Spring Boot 3.3.5 microservices + Python 3.12 FastAPI ML engine.
  - Apache Kafka event bus for high-throughput event processing.
  - Angular 17 Standalone Component Command Center (`/financial-risk`).
- **What to Say**: *"Our backend architecture comprises 8 Spring Boot microservices connected via Apache Kafka and PostgreSQL, serving an Angular 17 Command Center."* (20s)

---

### SLIDE 5: Relationship Graph & AI Behavioral Intelligence
- **Title**: **Catching Fraud Rings & Disambiguating Corporate Subnets**
- **Key Visual**: SVG Graph topology visual showing shared hardware device nodes.
- **Bullets**:
  - Detects shared device fingerprints (8 accounts) and reused payment tokens (3 accounts).
  - Improved evasive fraud recall from **$95.65\% \rightarrow 100.0\%$ (+4.35%)**.
  - Reduced false positive blocks on shared corporate subnets from **$6 \rightarrow 0$ FP**.
- **What to Say**: *"Single-transaction models miss multi-account collusion. Sentinel's in-memory graph engine catches multi-account fraud rings while preventing false blocks on shared corporate IPs."* (25s)

---

### SLIDE 6: Asymmetric Business Cost Decision Engine
- **Title**: **Cost-Aware 3-Tier Policy Thresholds**
- **Key Visual**: Decision threshold diagram ($\tau_{\text{block}} = 0.50$, $\tau_{\text{review}} = 0.25$) and visual SVG Cost Curve.
- **Bullets**:
  - **`APPROVE`** ($p < 0.25$): Instant pass with zero customer friction.
  - **`REVIEW`** ($0.25 \le p < 0.50$): Human-in-the-Loop analyst queue (₹400 cost).
  - **`BLOCK`** ($p \ge 0.50$): Automated mitigation avoiding ₹6,800 chargeback loss.
- **What to Say**: *"By evaluating decisions against real business economics, Sentinel routes ambiguous transactions to analyst REVIEW, preventing false block friction on high-value checkouts."* (25s)

---

### SLIDE 7: Live System Demonstration
- **Title**: **Live SOC Command Center Demonstration**
- **Key Visual**: Screenshot / Live view of `/financial-risk` dashboard.
- **Bullets**:
  - **Scenario A**: Legitimate grocery checkout $\rightarrow$ `APPROVE`.
  - **Scenario B**: Ambiguous corporate checkout $\rightarrow$ `REVIEW` (Human-in-the-Loop).
  - **Scenario C**: High-risk fraud ring $\rightarrow$ `BLOCK` $\rightarrow$ SOC Incident.
- **What to Say**: *"Let's move to our live demonstration at /financial-risk to evaluate Scenarios A, B, and C in real-time."* (30s)

---

### SLIDE 8: Explainable AI & Policy Simulation
- **Title**: **Explainable Attributions & Interactive Policy Simulator**
- **Key Visual**: Explainable AI panel screenshot & Policy Threshold Simulator sliders.
- **Bullets**:
  - Displays 4 primary detection reasons comparing observed values to 95th percentile baselines.
  - Risk officers can adjust sliders ($\tau_{\text{block}}$ & $\tau_{\text{review}}$) to simulate policy impact on loss.
- **What to Say**: *"Sentinel provides complete explainability for every decision, allowing risk officers to simulate policy threshold changes before deploying rules."* (20s)

---

### SLIDE 9: Business Impact & Honest Disclosures
- **Title**: **Quantifiable Value & Scientific Discipline**
- **Key Visual**: Model validity summary table & anti-leakage checklist.
- **Bullets**:
  - Zero target or temporal leakage during scaler fit and threshold tuning.
  - Disclosed dataset limitations (synthetic benchmark set requires re-training on 90-day production logs).
  - Clear production roadmap for distributed GNN embeddings and Flink streaming.
- **What to Say**: *"Sentinel was built with scientific discipline—strictly isolating training scalers and validation thresholds. We transparently disclose dataset limitations and present a realistic production roadmap."* (25s)

---

### SLIDE 10: Closing & Why Sentinel Wins
- **Title**: **Why Sentinel for Razorpay Track 2**
- **Key Visual**: 30-second summary box & team roles.
- **Bullets**:
  - **Complete & Working**: 9 microservices, Angular UI, Python ML, 100% test pass.
  - **Unique Value**: Graph + AI + Cost Optimization + SOC Incident Response.
  - **Track 2 Alignment**: Tailored specifically for payment gateway risk management.
- **What to Say**: *"Sentinel bridges AI research with operational financial security, delivering actionable risk management. Thank you!"* (20s)
