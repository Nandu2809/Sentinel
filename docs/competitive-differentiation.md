# Sentinel — Competitive Differentiation Analysis

## Overview
This document evaluates Sentinel against conventional security and fraud detection paradigms, detailing how Sentinel's integrated architecture delivers superior risk management.

---

## Comparative Matrix

| Category | What Generic Systems Do | What Sentinel Adds |
| :--- | :--- | :--- |
| **Rule-Based Fraud Engines** | Evaluate static thresholds (e.g., `amount > ₹50,000`). Fragile, easily bypassed, high false positive rate. | **Dynamic ML & Feature Fusion**: Integrates 34 behavioral and graph features with automated risk probability scoring. |
| **Traditional ML Classifiers** | Predict binary fraud labels (`0` or `1`) on isolated transaction records. Black-box output. | **Explainable AI & Graph Topology**: Provides feature attributions and multi-hop node relationship visualization. |
| **Pure Anomaly Detectors** | Flag any statistical outlier. Generates massive alert fatigue and false positive blocks. | **Cost-Aware Decision Optimization**: Optimizes operating thresholds against financial loss metrics ($C_{\text{FP}}$, $C_{\text{FN}}$). |
| **Graph-Based Fraud Detectors** | Require complex offline graph database queries (Neo4j/Gremlin). High latency, non-real-time. | **In-Memory Topology Feature Extraction**: Computes graph density and entity sharing in real-time within the Kafka stream consumer. |
| **Generic AI Risk Scoring** | Produce a raw numerical score without operational workflow or incident integration. | **Integrated SOC Incident Bridge**: Automatically converts high-risk events into SOC alerts (`sentinel.alert-events`) and incidents (`/incidents`). |
| **Manual Review Systems** | Require analysts to check multiple disconnected tools and databases manually. | **Unified SOC Command Center**: Integrates stream tables, graph visualization, policy simulator, and incident creation in a single Angular workstation (`/financial-risk`). |

---

## Core Differentiators Summary

> *"Sentinel's differentiator is the integration of high-throughput financial telemetry, in-memory relationship graph features, cost-sensitive 3-tier decisions, explainable AI attribution, and unified SOC incident response into a single production-ready platform."*
