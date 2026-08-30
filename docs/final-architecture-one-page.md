# Sentinel — Final Architecture One-Pager

## Overview
This document summarizes Sentinel's end-to-end architecture pipeline in one clean page.

---

## 📐 One-Page Architecture Flow

```
1. PAYMENT EVENT           Client transaction envelope (`FinancialRiskEvent`) submitted to API Gateway.
       │
       ▼
2. KAFKA INGESTION         Spring Cloud Gateway streams events to topic `sentinel.financial.events`.
       │
       ▼
3. FEATURE EXTRACTION      `FinancialRiskEventConsumer` extracts 34 behavioral & telemetry signals.
       │
       ▼
4. RELATIONSHIP GRAPH      In-memory topology engine measures multi-account hardware sharing & ring density.
       │
       ▼
5. AI / ML EVALUATION      Scikit-Learn Random Forest computes continuous risk probability ($0\text{--}100$).
       │
       ▼
6. RISK PROBABILITY SCORE  Combines model output with heuristic factors into continuous risk score.
       │
       ▼
7. BUSINESS COST OPT.     Cost loss function $C_{\text{total}}(\tau)$ evaluates financial trade-offs ($C_{\text{FP}}$ vs $C_{\text{FN}}$).
       │
       ▼
8. 3-TIER POLICY DECISION Categorizes event into `APPROVE` ($p<0.25$), `REVIEW` ($0.25\le p<0.50$), or `BLOCK` ($p\ge 0.50$).
       │
       ▼
9. ALERT DISPATCH          High-risk events stream to Kafka topic `sentinel.alert-events`.
       │
       ▼
10. INCIDENT CREATION     `alert-service` & `incident-service` auto-create SOC incident `INC-2026-1000`.
       │
       ▼
11. SOC COMMAND CENTER    Angular 17 UI (`/financial-risk`) renders SVG topology, Explainable AI, & incident links.
       │
       ▼
12. SOC ANALYST ACTION     Analyst investigates entity attributions and initiates mitigation in `/incidents`.
```
