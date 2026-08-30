# Sentinel — Production Roadmap

## Overview
This document clearly distinguishes Sentinel's current hackathon prototype from the future production deployment roadmap.

---

## 🗺️ Prototype vs Production Matrix

| Dimension | Current Hackathon Prototype | Future Production System (6-Month Horizon) |
| :--- | :--- | :--- |
| **Dataset & Weights** | Synthetic 10,000-record benchmark dataset ($8\%$ fraud rate). | Re-trained on 90+ days of historical production merchant payment logs. |
| **Graph Infrastructure** | In-memory graph feature computation per Kafka event stream. | Persistent distributed graph database clusters (Memgraph / Neo4j / AWS Neptune). |
| **AI Models** | Scikit-learn Random Forest & Isolation Forest anomaly models. | Sub-millisecond Graph Neural Networks (GNNs) using PyTorch Geometric. |
| **Stream Ingestion** | Apache Kafka on confluentinc docker setup. | Multi-region Apache Flink / Kafka cluster with active-active geo-replication. |
| **Secrets Management** | Docker environment file fallbacks (`.env`). | HashiCorp Vault or AWS Secrets Manager with automatic key rotation. |
| **Model Monitoring** | Static validation benchmark metrics. | Automated continuous model drift detection (Evidently AI / MLflow) and auto-retraining. |
| **Policy Tuning** | Manual slider simulator in frontend component. | Reinforcement learning policy agent tuning decision thresholds dynamically per merchant. |
