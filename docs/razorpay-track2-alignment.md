# Sentinel — Alignment with Razorpay Track 2: AI Risk Manager

## Track Alignment Overview
This document maps Sentinel's core capabilities directly to the judging criteria for **Razorpay Hackathon Track 2 ("AI Risk Manager")**.

---

## 🎯 Alignment Matrix

| Track 2 Requirement | How Sentinel Solves It | Implementation Evidence |
| :--- | :--- | :--- |
| **Real-Time Financial Risk Scoring** | Ingests transaction envelopes (`FinancialRiskEvent`), extracts 34 features, and computes risk probability scores ($0\text{--}100$). | `risk-service/RiskCalculationService.java`, `FinancialRiskController.java` |
| **Relationship & Fraud Ring Detection** | Computes in-memory graph topology features (hardware fingerprint sharing, IP subnet concentration, payment token reuse). | `FinancialRelationshipGraphComponent.ts`, `features_enhanced.csv` |
| **AI & Behavioral Intelligence** | Combines Random Forest ML models, Isolation Forest anomaly scoring, and Explainable AI feature attributions. | `ai-engine/`, Explainable AI panel in `/financial-risk` |
| **Decisioning & Cost Optimization** | 3-tier policy model (`APPROVE`, `REVIEW`, `BLOCK`) optimized against false positive friction vs chargeback loss economics. | Phase 6E business cost model ($\tau^* = 0.50$), Policy Threshold Simulator |
| **Customer Friction Reduction** | Disambiguates shared corporate subnets, reducing false positive blocks on legitimate corporate checkouts from $6 \rightarrow 0$ FP. | Benchmark evaluation on `LEGITIMATE_SHARED_INFRASTRUCTURE` scenario |
| **Human-in-the-Loop Review** | Routes ambiguous events ($0.25 \le p < 0.50$) to analyst `REVIEW` queue with interactive indicator banner. | `REVIEW` state banner in `/financial-risk` component |
| **Explainable AI (XAI)** | Displays 4 primary detection reasons comparing observed signal values against 95th percentile baselines. | Explainable AI panel in `/financial-risk` |
| **Operational Incident Response** | Emits critical alerts to `sentinel.alert-events`, creates active SOC incidents (`INC-2026-1000`), and bridges to threat hunting. | `alert-service`, `incident-service`, `/incidents` workspace |
