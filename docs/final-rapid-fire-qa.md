# Sentinel — Rapid-Fire Judge Q&A Database (20 Questions)

## ⚡ 10–20 Second Spoken Answers

### 1. What exactly is Sentinel?
*"Sentinel is an explainable, relationship-aware, cost-sensitive AI Risk Management Platform built for Razorpay Track 2."* (10s)

### 2. Why AI?
*"AI replaces rigid static rules with dynamic evaluation over 34 behavioral and graph features to catch evolving anomalies."* (12s)

### 3. Why graph?
*"Graph features detect multi-account hardware sharing and payment token reuse that transaction-level features miss."* (12s)

### 4. Why Kafka?
*"Kafka provides high-throughput event streaming that decouples ingestion from downstream ML scoring and SOC alerting."* (12s)

### 5. Why Random Forest?
*"Random Forest handles non-linear tabular data cleanly without extensive tuning and natively outputs feature importances."* (12s)

### 6. Why not deep learning?
*"Deep learning on tabular data requires massive dataset sizes, increases latency, and lacks native feature explainability."* (12s)

### 7. What is unique?
*"The integration of in-memory graph topology, asymmetric business cost optimization, and SOC incident response."* (12s)

### 8. How does cost-aware decisioning work?
*"It evaluates risk decisions against real business penalties ($C_{\text{FP}} = \text{₹1,200}$ vs $C_{\text{FN}} = \text{₹6,800}$) to minimize business loss."* (15s)

### 9. Why REVIEW?
*"`REVIEW` provides a human-in-the-loop buffer for ambiguous events, avoiding false block customer friction."* (12s)

### 10. How does this reduce false positives?
*"Graph topology disambiguates shared corporate subnets, reducing false blocks on corporate users from 6 FP to 0 FP."* (15s)

### 11. What happens with false negatives?
*"Missed fraud triggers post-chargeback graph analysis, updating risk weights for subsequent transactions in the ring."* (15s)

### 12. Is the dataset real?
*"No. It is a synthetic benchmark dataset of 10,000 records designed with 8 fraud scenarios to benchmark model logic safely."* (15s)

### 13. Why is AUC 1.0?
*"Complete non-linear separability of generated scenario rules on our synthetic benchmark set under strict anti-leakage isolation."* (15s)

### 14. How do you prevent leakage?
*"Scalers were fitted exclusively on `train.csv`, thresholds tuned on `validation.csv`, and labels stripped prior to matrix creation."* (15s)

### 15. Can it scale?
*"Yes—stateless microservices scale via Kubernetes HPA, Kafka partitions distribute stream load, and Redis caches state."* (15s)

### 16. How would Razorpay integrate it?
*"Razorpay payment webhooks or Kafka topics would stream transaction envelopes into `FinancialRiskEventConsumer`."* (12s)

### 17. Is it production-ready?
*"The 9 Spring Boot microservices and Angular UI are production-ready; model weights require re-training on 90-day production logs."* (15s)

### 18. Biggest limitation?
*"Recall tradeoff on low-velocity `LOW_AND_SLOW_RING` fraud, proving graph features complement transaction features."* (15s)

### 19. Biggest innovation?
*"Fusing relationship graph topology, cost-sensitive 3-tier decisions, Explainable AI, and SOC incident workflows into one platform."* (15s)

### 20. Why should we select Sentinel?
*"Because Sentinel is a complete, working platform that bridges AI research with operational financial security, delivering clear business value."* (15s)
