# Sentinel — Final Presentation Deck Content (10 Master Slides)

## Overview
This document contains the exact title, bullets, visuals, spoken commentary, and timing for all 10 presentation slides.

---

## 🎨 Slide Master Outline

### SLIDE 1: Title & Track Alignment
- **Slide Title**: **SENTINEL — AI Financial Risk Manager**
- **Bullets (Max 5)**:
  - Razorpay Hackathon Track 2 ("AI Risk Manager")
  - Real-Time Financial Telemetry & Relationship Topology Graph
  - Asymmetric Business Cost Decision Engine
  - Integrated SOC Incident Response & Threat Hunting Workspace
- **Recommended Visual**: Sentinel dark-mode logo, command center badge, Razorpay Track 2 banner.
- **Presenter Spoken Script**: *"Good morning judges. We are presenting Sentinel—an explainable, relationship-aware, cost-sensitive AI Risk Management platform built for Razorpay Track 2."*
- **Duration**: 20 seconds.

---

### SLIDE 2: The Problem
- **Slide Title**: **The Dual Dilemma: Chargebacks vs False Block Friction**
- **Bullets (Max 5)**:
  - Missed evasive fraud leads to devastating chargebacks ($C_{\text{FN}} = \text{₹6,800}$).
  - Over-aggressive rules block legitimate customers ($C_{\text{FP}} = \text{₹1,200}$).
  - Multi-account fraud rings evade single-transaction rule engines.
  - Legacy tools output raw binary labels with zero cost context.
- **Recommended Visual**: Split graphic contrasting Chargeback Penalty (₹6,800) vs False Block Churn (₹1,200).
- **Presenter Spoken Script**: *"Payment gateways face two major threats: undetected fraud rings cause chargebacks, while rigid rules block innocent checkouts, destroying customer trust."*
- **Duration**: 25 seconds.

---

### SLIDE 3: Why Fraud Classification Alone Is Not Enough
- **Slide Title**: **Moving From Binary Labels to Operational Risk Management**
- **Bullets (Max 5)**:
  - Binary models ($0$ vs $1$) provide zero explanation of *why* risk is elevated.
  - Isolated transaction evaluation ignores multi-account device sharing.
  - Lack of operational bridge leaves security teams in disconnected silos.
  - Sentinel transforms risk classification into actionable SOC response.
- **Recommended Visual**: Flow diagram showing Binary Classifier (Black Box) vs Sentinel Operational Pipeline.
- **Presenter Spoken Script**: *"A model predicting 0.85 fraud probability tells an analyst nothing about why the score is high or what entity relationships surround it. Sentinel bridges classification with operational security."*
- **Duration**: 25 seconds.

---

### SLIDE 4: Sentinel Architecture
- **Slide Title**: **Event-Driven Cloud-Native Architecture**
- **Bullets (Max 5)**:
  - 8 Java 21 Spring Boot 3.3.5 microservices + Python 3.12 FastAPI ML engine.
  - High-throughput Apache Kafka event bus (`sentinel.financial.events`).
  - PostgreSQL schema isolation & Redis rate limiting.
  - Standalone Angular 17 Command Center (`/financial-risk`).
- **Recommended Visual**: Microservices architecture block diagram with Kafka event flow.
- **Presenter Spoken Script**: *"Our backend architecture comprises 8 Spring Boot microservices connected via Apache Kafka and PostgreSQL, serving an Angular 17 Command Center."*
- **Duration**: 20 seconds.

---

### SLIDE 5: Relationship + AI Intelligence
- **Slide Title**: **Multi-Hop Topology Graph & Behavioral AI**
- **Bullets (Max 5)**:
  - In-memory graph feature extraction across 34 telemetry fields.
  - Detects shared hardware fingerprints (8 accounts) & payment token reuse.
  - Improved evasive fraud recall from **$95.65\% \rightarrow 100.0\%$ (+4.35%)**.
  - Reduced false blocks on corporate subnets from **$6 \rightarrow 0$ FP**.
- **Recommended Visual**: Animated SVG Relationship Topology Graph showing red fraud ring nodes.
- **Presenter Spoken Script**: *"Single-transaction models miss multi-account collusion. Sentinel's graph engine catches multi-account fraud rings while preventing false blocks on shared corporate subnets."*
- **Duration**: 25 seconds.

---

### SLIDE 6: Cost-Aware Risk Decisioning
- **Slide Title**: **Optimizing Financial Loss via Business Cost Equations**
- **Bullets (Max 5)**:
  - Minimizes total expected business loss $C_{\text{total}}(\tau)$.
  - Incorporates false block penalties ($C_{\text{FP}}$) & chargeback losses ($C_{\text{FN}}$).
  - Validation-optimal block threshold set at $\tau^* = 0.50$.
  - Interactive Risk Policy Simulator for real-time offline testing.
- **Recommended Visual**: Business Cost Curve SVG graph highlighting minimum cost at $\tau^* = 0.50$.
- **Presenter Spoken Script**: *"By evaluating decisions against real business economics, Sentinel balances false block penalties against chargeback losses to minimize total expected financial cost."*
- **Duration**: 20 seconds.

---

### SLIDE 7: APPROVE / REVIEW / BLOCK
- **Slide Title**: **3-Tier Policy Thresholds & Human-in-the-Loop**
- **Bullets (Max 5)**:
  - **`APPROVE`** ($p < 0.25$): Instant checkout pass with ₹0 expected loss.
  - **`REVIEW`** ($0.25 \le p < 0.50$): Human-in-the-loop analyst queue (₹400 cost).
  - **`BLOCK`** ($p \ge 0.50$): Automated mitigation avoiding ₹6,800 chargeback loss.
  - Analyst indicator banner displayed on intermediate confidence events.
- **Recommended Visual**: 3-Tier Policy State diagram highlighting the `REVIEW` human-in-the-loop buffer.
- **Presenter Spoken Script**: *"Sentinel operates a 3-tier policy model. Routing ambiguous events to analyst REVIEW prevents insulting legitimate customers with unnecessary blocks."*
- **Duration**: 20 seconds.

---

### SLIDE 8: Live Demo
- **Slide Title**: **Live Command Center Demonstration**
- **Bullets (Max 5)**:
  - **Scenario A**: Legitimate grocery checkout $\rightarrow$ `APPROVE`.
  - **Scenario B**: Ambiguous corporate IP purchase $\rightarrow$ `REVIEW` (Human-in-the-Loop).
  - **Scenario C**: Coordinated fraud ring $\rightarrow$ `BLOCK` $\rightarrow$ SOC Incident.
  - Real-time SVG topology graph inspection & Explainable AI attributions.
- **Recommended Visual**: Screenshot / Live screen capture of `/financial-risk` command center.
- **Presenter Spoken Script**: *"Let's move to our live demonstration at /financial-risk to evaluate Scenarios A, B, and C in real-time."*
- **Duration**: 30 seconds.

---

### SLIDE 9: Business Value + Honest Limitations
- **Slide Title**: **Quantifiable Value & Transparent Disclosures**
- **Bullets (Max 5)**:
  - Zero target or temporal leakage during scaler fit & threshold tuning.
  - Reduced corporate false blocks to zero; eliminated chargebacks on rings.
  - Synthetic benchmark disclosures (10k records, requires production re-training).
  - Production roadmap: Flink streaming, GNN embeddings, Vault secrets.
- **Recommended Visual**: Business impact matrix & production roadmap timeline.
- **Presenter Spoken Script**: *"Sentinel delivers clear business value with scientific discipline. We transparently disclose dataset limitations and present a realistic 6-month production roadmap."*
- **Duration**: 25 seconds.

---

### SLIDE 10: Why Sentinel?
- **Slide Title**: **The Complete AI Financial Risk Manager**
- **Bullets (Max 5)**:
  - **Complete System**: 9 microservices, Angular 17 UI, Python ML, 100% test pass.
  - **Differentiator**: Graph + AI + Cost Optimization + SOC Incident Response.
  - **Track 2 Alignment**: Tailored specifically for payment gateway risk management.
- **Recommended Visual**: Summary box displaying the 30-second pitch statement.
- **Presenter Spoken Script**: *"Sentinel transforms payment telemetry into actionable security decisions—stopping fraud rings while protecting customer conversion. Thank you!"*
- **Duration**: 20 seconds.
