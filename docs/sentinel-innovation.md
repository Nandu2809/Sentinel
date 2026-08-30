# Sentinel — Innovation Statement

## Executive Summary
This document frames Sentinel's technical innovation as **product capability**, avoiding generic technology buzzwords.

---

## 💡 What is the Real Innovation?

> *"Sentinel's innovation is NOT simply using Kafka, Spring Boot, or Python. Its innovation is the **functional fusion** of relationship-aware graph intelligence, asymmetric business loss optimization, explainable attribution, and automated SOC incident response into a unified real-time payment risk platform."*

---

## 6 Product Capability Innovations

1. **In-Memory Relationship Graph Feature Extraction**:
   - Rather than relying on slow offline graph queries, Sentinel extracts node degree, ring density, hardware fingerprint sharing, and payment token reuse directly inside the real-time stream consumer.

2. **Asymmetric Business Cost Decision Optimization**:
   - Sentinel evaluates risk decisions ($\tau_{\text{block}} = 0.50$, $\tau_{\text{review}} = 0.25$) against real business economics, balancing false positive block costs ($C_{\text{FP}} = \text{₹1,200}$) against false negative chargebacks ($C_{\text{FN}} = \text{₹6,800}$).

3. **Human-in-the-Loop 3-Tier Policy Buffer**:
   - Instead of forcing binary pass/block choices, Sentinel routes ambiguous transactions ($0.25 \le p < 0.50$) to analyst `REVIEW` at ₹400 cost, preventing false positive customer friction on high-value checkouts.

4. **Explainable AI Attribution ("Why This Decision?")**:
   - Renders human-readable primary detection reasons in real-time, comparing observed feature values against 95th percentile baselines.

5. **Integrated SOC Incident Response Bridge**:
   - High-risk events automatically emit to `sentinel.alert-events`, generating active SOC incidents (`INC-2026-1000`) with one-click navigation to threat hunting workspaces.

6. **Interactive Risk Policy Simulator**:
   - Allows risk officers to adjust threshold sliders ($\tau_{\text{block}}$ & $\tau_{\text{review}}$) to simulate policy impact on approval rates and financial losses before mutating production rules.
