# Sentinel — How to Explain the System Architecture (60 Seconds)

## Executive Summary
This document provides a simple, clean 60-second explanation of Sentinel's multi-service architecture for judges.

---

## 🎙️ The 60-Second Spoken Script

> *"Sentinel's architecture follows a clean, event-driven microservices pattern:
>
> 1. **Client & Ingestion Layer**: Client checkouts hit our **API Gateway**, which validates JWT tokens and applies Redis rate limiting before forwarding transaction envelopes (`FinancialRiskEvent`) to **Apache Kafka** on topic `sentinel.financial.events`.
> 
> 2. **Analytical Engine**: Our Java 21 **Risk Service** consumes stream events, extracts 34 features—including in-memory multi-hop **Relationship Graph Topology**—and invokes our Python **AI Engine** for behavioral anomaly scoring.
> 
> 3. **Cost-Aware Decisioning**: The **Risk Engine** applies asymmetric business cost optimization to determine whether to `APPROVE`, route to analyst `REVIEW`, or `BLOCK`.
> 
> 4. **SOC Operational Bridge**: High-risk events emit to Kafka topic `sentinel.alert-events`, triggering our **Alert Service** and **Incident Service** to auto-generate active SOC incidents (`INC-2026-1000`).
> 
> 5. **Frontend Command Center**: Security teams monitor live transactions, inspect SVG topology graphs, and manage incidents inside our Angular 17 Command Center at `/financial-risk`."*
