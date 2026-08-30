# Sentinel — Final Hackathon Pitch Suite

## Overview
This document contains 4 tailored presentation pitch formats (30-second elevator pitch, 60-second summary, 2-minute pitch, and 5-minute competition pitch) for **Razorpay Hackathon Track 2 — AI Risk Manager**.

---

## 🎯 The Central Storyline

Traditional fraud detection asks:
> *"Is this transaction fraudulent?"*

Sentinel asks:
> *"How risky is this transaction, **WHY** is it risky, **WHAT** entity relationships surround it, **WHAT** will it cost if we are wrong, and **WHAT** should the organization do next?"*

---

## ⏱️ Pitch Formats

### A. 30-Second Elevator Pitch
> *"Traditional payment fraud systems suffer from binary classification tunnel vision and isolated transaction processing. Presenting **Sentinel** — an explainable, relationship-aware, cost-sensitive AI Risk Management Platform built for Razorpay Track 2.
>
> Sentinel ingests real-time financial telemetry via Kafka, extracts 34 behavioral and in-memory graph topology features, optimizes decisions against asymmetric business costs, and automatically bridges high-risk threats into SOC incident response workflows."*

### B. 60-Second Summary Pitch
> *"Every day, payment gateways face a dual threat: undetected fraud rings cause devastating chargebacks, while over-aggressive fraud rules insult legitimate customers with false blocks.
>
> **Sentinel** solves this by unifying seven core capabilities into a single platform: Financial Telemetry Ingestion + In-Memory Relationship Graph Topology + AI Behavioral Anomaly Detection + Machine Learning Risk Scoring + Business Cost Decision Optimization + Human-in-the-Loop Analyst Review + SOC Incident Response.
>
> Instead of forcing a binary block or pass, Sentinel operates a 3-tier policy model: `APPROVE` ($p < 0.25$), analyst `REVIEW` ($0.25 \le p < 0.50$), and `BLOCK` ($p \ge 0.50$). This minimizes expected business loss, eliminates false positive friction on shared subnets, and empowers security teams with clear Explainable AI attributions."*

### C. 2-Minute Keynote Pitch
> *"Good morning judges. In payment security, simple fraud classification is broken. A model predicting '0.85 fraud probability' tells an analyst nothing about why the score is high, what entity relationships surround it, or whether blocking it costs more than allowing it.
>
> We built **Sentinel** — an enterprise AI Risk Management platform designed for Razorpay Track 2. Sentinel operates a high-throughput event pipeline built on Java 21 Spring Boot microservices, Apache Kafka, Python ML, and an Angular 17 SOC Command Center.
>
> As transactions stream in, Sentinel constructs an in-memory relationship topology graph connecting Users, Hardware Device Fingerprints, IP Subnets, Payment Token References, and Merchants across 34 extracted features. This allows Sentinel to catch multi-account fraud rings that transaction-level models completely miss—improving evasive fraud recall from 95.65% to 100%—while preventing false blocks on shared corporate subnets.
>
> Crucially, Sentinel is cost-aware. It evaluates decisions against asymmetric business penalties: false block penalties ($C_{\text{FP}} = \text{₹1,200}$), false negative chargebacks ($C_{\text{FN}} = \text{₹6,800}$), analyst investigation fees ($C_{\text{REVIEW}} = \text{₹400}$), and friction ($C_{\text{FRICTION}} = \text{₹160}$).
>
> High-risk events are automatically emitted to `sentinel.alert-events`, generating active SOC incidents (`INC-2026-1000`) with one-click navigation to threat hunting workspaces. Sentinel doesn't just classify risk—it manages it end-to-end."*

### D. 5-Minute Competition Presentation Pitch
*(Reference [`docs/final-5-minute-demo.md`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/docs/final-5-minute-demo.md) for full minute-by-minute live presentation script).*
