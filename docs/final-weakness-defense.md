# Sentinel — Final Weakness Defense & Production Engineering

## Executive Summary
This document addresses 8 actual weaknesses in Sentinel's prototype, demonstrating engineering maturity through transparent disclosure.

---

## 🛠️ Weakness & Production Solution Matrix

| Weakness / Limitation | Current Prototype Reality | Why It Exists in Prototype | How Production Would Solve It |
| :--- | :--- | :--- | :--- |
| **1. Synthetic Dataset** | 10,000 synthetic transaction records with 8 fraud scenarios ($8\%$ fraud rate). | Designed to benchmark model performance safely without PII or NDA constraints. | Re-train model weights on 90+ days of historical production merchant payment logs. |
| **2. Benchmark Dataset Size** | 10,000 rows (7k Train, 1k Val, 2k Test). | Capped to enable sub-second unit test execution and fast CI/CD builds. | Scale dataset pipelines to millions of transaction logs in Apache Spark / BigQuery. |
| **3. In-Memory Graph Engine** | Graph topology computed in-memory per Kafka event stream consumer. | Achieves sub-millisecond execution for hackathon presentation without external database setup. | Deploy distributed persistent graph database clusters (Memgraph / Neo4j / AWS Neptune). |
| **4. Production Scaling** | Single-node Docker Compose setup with 14 container services. | Provides standalone local spin-up for demonstration simplicity. | Deploy onto multi-region Kubernetes clusters with Horizontal Pod Autoscalers (HPA). |
| **5. Model Concept Drift** | Model weights are static once loaded. | Continuous retraining pipelines were out of scope for hackathon timeline. | Deploy automated model monitoring (Evidently AI / MLflow) with scheduled retraining DAGs. |
| **6. Real-World Calibration** | Cost parameters ($C_{\text{FP}} = \text{₹1,200}$, $C_{\text{FN}} = \text{₹6,800}$) are synthetic values. | Used to validate cost-aware decision optimization algorithms. | Calibrate cost parameters per merchant based on actual chargeback fees and profit margins. |
| **7. Merchant-Specific Behavior** | Single global risk decision boundary ($\tau^* = 0.50$). | Simplifies presentation narrative across demo scenarios. | Implement multi-tenant policy agents allowing custom threshold sliders per merchant profile. |
| **8. Secret Hardening** | Environment variable fallbacks in `docker-compose.yml`. | Enables instant local developer spin-up without Vault setup. | Inject production credentials via HashiCorp Vault or AWS Secrets Manager with key rotation. |
