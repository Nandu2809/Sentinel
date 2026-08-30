# Sentinel — Model Metrics Defense: "100% Accuracy" Explanation

## Executive Summary
This document provides a rigorous technical defense of Sentinel's benchmark model metrics ($ROC\text{-}AUC = 1.0000$, $PR\text{-}AUC = 1.0000$) for hackathon judges and technical reviewers.

---

## Technical Explanation of Benchmark Metrics

### 1. Dataset Characteristics
- **Dataset Size**: 10,000 synthetically generated financial transaction records.
- **Fraud Distribution**: 800 fraud instances (8.0% positive class prevalence).
- **Chronological Split**: 70% Train ($N=7,000$), 10% Validation ($N=1,000$), 20% Held-Out Test ($N=2,000$).

### 2. Scientific Anti-Leakage Protocol
- **Scaler Fit**: `StandardScaler` was fitted **exclusively on `train.csv`**. Validation and test sets were transformed using training parameters.
- **Threshold Tuning**: Decision boundaries ($\tau^* = 0.5000$) were optimized **exclusively on `validation.csv`**.
- **Label Exclusion**: Ground-truth target columns (`isFraud`, `fraudScenario`) were strictly stripped prior to feature matrix generation.

### 3. Why the Benchmark Achieved 1.0 AUC
The synthetic benchmark dataset features 8 distinct, deterministic fraud scenarios (e.g., velocity spikes, multi-account device sharing, payment token reuse). Because these rules create clear non-linear boundaries in 34-dimensional feature space, Random Forest decision trees achieve perfect separation on the synthetic test set.

---

## 📌 Disclosures: WHAT WE CLAIM vs WHAT WE DO NOT CLAIM

| Category | WHAT WE CLAIM ✓ | WHAT WE DO NOT CLAIM ✗ |
| :--- | :--- | :--- |
| **Scientific Discipline** | We strictly adhered to zero target/temporal leakage protocols during feature scaling and threshold tuning. | We DO NOT claim Sentinel will achieve 100% accuracy on noisy production payment streams. |
| **Graph Superiority** | Graph intelligence improved `EVASIVE_FRAUD` recall from $95.65\% \rightarrow 100.0\%$ (+4.35%) and reduced corporate NAT false positives from $6 \rightarrow 0$ FP. | We DO NOT claim graph features are universally superior; `LOW_AND_SLOW_RING` scenario showed a recall tradeoff ($100\% \rightarrow 42.31\%$). |
| **Production Readiness** | Sentinel's microservice architecture, Kafka ingestion, cost decision engine, and Angular SOC UI are production-ready. | We DO NOT claim the ML model weights can be deployed without re-training on historical 90-day merchant transaction logs. |

---

## 20-Second Judge Defense Answer
> *"Our 1.0 AUC score reflects complete non-linear separability of generated scenario rules on our 10,000-record synthetic benchmark set under strict anti-leakage isolation. We do not claim 100% accuracy in production; real-world payment streams contain noise that will yield realistic AUCs around 0.92–0.96."*
