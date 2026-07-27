# Sentinel AI Engine Architecture

## Overview
The Sentinel AI Engine is an autonomous, real-time threat intelligence and behavior-based anomaly detection microservice designed to complement Sentinel's Java Spring Boot rule-based engines.

## Hybrid Security Model
```
 +-------------------------------------------------------------------------+
 |                              API TELEMETRY                              |
 +-------------------------------------------------------------------------+
                         |                           |
                         v                           v
         +-------------------------------+  +--------------------------+
         |  Rule Engine (Threat Service) |  |  AI Anomaly Engine       |
         |  - OWASP Top 10 Signatures   |  |  - Isolation Forest      |
         |  - Regex & Path Traversal    |  |  - One-Class SVM         |
         |  - Known Threat Payloads     |  |  - Density Clustering    |
         +-------------------------------+  +--------------------------+
                         \                           /
                          v                         v
         +-------------------------------------------------------------+
         |             Multi-Factor Risk Scoring Engine                |
         |  Fused Risk Score = (0.4 * Rule) + (0.4 * AI) + (0.2 * Asset) |
         +-------------------------------------------------------------+
```

## System Components
1. **Feature Extractor**: Computes numerical vectors (frequency, log payload sizes, URI entropy, latency, authentication velocity).
2. **Preprocessing Pipeline**: Standardizes continuous variables using `StandardScaler`.
3. **ML Ensemble**: Combines Isolation Forest, One-Class SVM, and Cluster distance thresholds.
4. **Explainable AI (XAI)**: Generates human-readable primary detection reasons and feature attribution scores.
5. **Model Registry**: Manages versioning, artifact persistence, and metric auditing.
