# Sentinel Track 2 — Judge-Facing Capability & Evidence Matrix

## Razorpay Hackathon Track 2: AI Risk Manager

This matrix provides direct code locations, verification tests, and evidence for every capability claimed in Sentinel.

| Capability Area | Core Feature / Claim | Implementation Location | Evidence & Verification |
| :--- | :--- | :--- | :--- |
| **1. Event-Driven Architecture** | Kafka Ingestion (`sentinel.financial.events`) | [`FinancialRiskEventConsumer.java`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/backend/risk-service/src/main/java/com/sentinel/risk/event/FinancialRiskEventConsumer.java) | Tested in unit tests & Kafka verification script |
| **2. Relationship Graph Intelligence** | 25 In-Memory Topology Features | [`hardened_feature_pipeline.py`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/ai-engine/anomaly-detection/app/financial/hardened_feature_pipeline.py) | Verified in Phase 6C & Phase 6F benchmark datasets |
| **3. Machine Learning Models** | Baseline (9) vs Enhanced (34) RF & Logistic Regression | [`phase6f_ml_experiment.py`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/ai-engine/anomaly-detection/app/financial/phase6f_ml_experiment.py) | Documented in `docs/financial-ml-baseline-vs-enhanced.md` |
| **4. Cost-Aware Decision Engine** | 3-Tier Policy (`APPROVE`, `REVIEW`, `BLOCK`) | [`RiskDecisionEngine.java`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/backend/risk-service/src/main/java/com/sentinel/risk/service/RiskCalculationService.java) | $C_{\text{FP}} = \text{₹1,200}$, $C_{\text{FN}} = \text{₹6,800}$ optimization |
| **5. Anti-Leakage Discipline** | Exclusively `train.csv` fit & validation thresholding | [`test_phase6f_financial.py`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/tests/test_phase6f_financial.py) | Passed `test_no_target_or_future_leakage` unit test |
| **6. SOC Incident Bridge** | Financial Risk -> Alert -> Incident | [`RiskCalculationService.java`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/backend/risk-service/src/main/java/com/sentinel/risk/service/RiskCalculationService.java) | Automatic `AlertEventEnvelope` dispatch to Phase 5 SOC |
| **7. Policy Threshold Simulator** | Safe UI simulation of $\tau_{\text{block}}$ & $\tau_{\text{review}}$ | [`financial-risk.component.ts`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/frontend/sentinel/src/app/features/financial-risk/financial-risk.component.ts) | Live cost curve rendering without backend state mutation |
| **8. Interactive Relationship Graph** | SVG Multi-hop Topology Visualization | [`financial-relationship-graph.component.ts`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/frontend/sentinel/src/app/features/financial-risk/financial-relationship-graph.component.ts) | Interactive zoom, pan, node selection inspector |
| **9. Angular Command Center UI** | Cyber Intelligence Command Center | [`financial-risk.component.ts`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/frontend/sentinel/src/app/features/financial-risk/financial-risk.component.ts) | Angular 17 production build verified (`dist/sentinel`) |
| **10. Honest Model Limitations** | Documented LOW_AND_SLOW_RING tradeoff | [`phase6h-model-validity.md`](file:///c:/Users/M.%20Devika%20Rani/Desktop/Sentinel/docs/phase6h-model-validity.md) | Transparently disclosed in Phase 6F metrics & UI |
