# Sentinel — Why Sentinel Beats a Normal Fraud Model

## Executive Summary
This document provides a simple, high-impact structural comparison between conventional binary fraud models and Sentinel's integrated AI Risk Management platform.

---

## 🔄 Architectural Comparison

### CONVENTIONAL FRAUD MODEL
```
Transaction Event
       │
       ▼
Binary ML Classifier
       │
       ▼
Fraud (1) / Not Fraud (0)
```
- **Limitations**:
  - Treats each transaction as an isolated event.
  - Outputs a black-box binary label or raw probability.
  - Ignores financial cost asymmetry ($C_{\text{FP}}$ vs $C_{\text{FN}}$).
  - Provides zero operational workflow or incident response.

---

### SENTINEL AI RISK MANAGER
```
Financial Transaction Telemetry (Kafka Envelope)
       │
       ▼
34 Financial + Relationship Graph Features
       │
       ▼
AI Behavioral Anomaly Engine
       │
       ▼
Multi-Factor Risk Probability Score (0–100)
       │
       ▼
Asymmetric Business Cost Optimization Model
       │
       ▼
3-Tier Policy Decision Engine (APPROVE / REVIEW / BLOCK)
       │
 ┌─────┴─────┐
 ▼           ▼
Kafka Alert  PostgreSQL
 Topic       Risk DB
 │
 ▼
Alert & Incident Microservices
 │
 ▼
Sentinel SOC Command Center (/financial-risk)
 │
 ▼
Analyst Incident Response & Threat Hunting Workspace
```

---

## ⚡ Why This Matters for Judges
1. **From Classification to Management**: Sentinel moves beyond asking *"Is this fraud?"* to actively managing financial risk, business costs, and security operations.
2. **Multi-Account Ring Defense**: In-memory graph topology captures coordinated fraud rings operating across multiple accounts that single-transaction models miss.
3. **Operational Bridge**: Integrates ML risk scoring directly with SOC incident management (`/incidents`), eliminating security operational silos.
