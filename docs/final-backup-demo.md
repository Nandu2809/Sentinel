# Sentinel — Final Backup Demo & Offline Presentation Protocol

## Overview
This document specifies the fallback sequence if live infrastructure or network connections fail during hackathon judging.

> ⚠️ **TRANSPARENCY RULE**: Never pretend a recorded or static backup is a live execution. If live services drop, be completely transparent: *"We are presenting our pre-verified empirical benchmark evidence while Docker reconnects."*

---

## 🛡️ Backup Presentation Sequence

```
1. PRE-VERIFIED SCREENSHOTS & UI STATES
                      │
                      ▼
2. PRE-RECORDED 5-MINUTE DEMO VIDEO
                      │
                      ▼
3. ARCHITECTURE DIAGRAM & DATA FLOW
                      │
                      ▼
4. EMPIRICAL BENCHMARK METRICS (Phase 6F Evaluation)
                      │
                      ▼
5. SCENARIO DETAILED BREAKDOWN MATRIX
                      │
                      ▼
6. SOURCE CODE VERIFICATION EVIDENCE
```

---

## Backup Evidence Artifact Locations

1. **System Architecture**: [`docs/final-system-architecture.md`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/docs/final-system-architecture.md)
2. **Benchmark Results**: [`data/financial/evaluation/benchmark_summary.json`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/data/financial/evaluation/benchmark_summary.json)
3. **Scenario Metrics**: [`data/financial/evaluation/scenario_metrics.csv`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/data/financial/evaluation/scenario_metrics.csv)
4. **Python ML Pipeline**: [`tests/test_phase6f_financial.py`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/tests/test_phase6f_financial.py)
5. **Spring Controller**: [`backend/risk-service/.../FinancialRiskController.java`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/backend/risk-service/src/main/java/com/sentinel/risk/controller/FinancialRiskController.java)
