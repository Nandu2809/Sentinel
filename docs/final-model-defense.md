# Sentinel — Final Model Defense & Synthetic vs Production Accuracy

## Executive Summary
This document prepares a bulletproof defense for judges asking about model benchmark metrics vs real-world production performance.

---

## 🎯 Direct Judge Defense Guide

### Question: "Do you have 100% real-world accuracy?"
### Answer:
> **"NO.**
> 
> Our 1.0 ROC-AUC score is achieved on a 10,000-record synthetic benchmark set designed with 8 discrete fraud scenario rules under strict zero-leakage isolation.
> 
> In production, real-world payment data contains unstructured noise, incomplete telemetry, and unexpected fraud vectors. On live merchant payment streams, we expect realistic ROC-AUC metrics in the **0.92 to 0.96** range."

---

## 🔬 Benchmark Methodology Summary

1. **Dataset Split**: Chronological 70% Train ($N=7,000$), 10% Validation ($N=1,000$), 20% Held-Out Test ($N=2,000$).
2. **Anti-Leakage Isolation**:
   - `StandardScaler` parameters fitted **exclusively on `train.csv`**.
   - Operating thresholds ($\tau^* = 0.5000$) tuned **exclusively on `validation.csv`**.
   - Ground-truth target columns (`isFraud`, `fraudScenario`) completely excluded from feature input matrices.
3. **Scientific Disclosures**:
   - High synthetic separability accounts for perfect benchmark scores.
   - Graph features exhibited a recall tradeoff ($100\% \rightarrow 42.31\%$) on low-velocity `LOW_AND_SLOW_RING` fraud, demonstrating that graph features complement rather than replace baseline transaction features.
