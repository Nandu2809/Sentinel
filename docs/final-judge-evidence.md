# Sentinel — Final Judge Evidence Package

## Overview
This matrix provides exact location pointers and verification methods for all 11 platform capabilities.

---

## 🔬 Evidence Location Matrix

| Platform Capability | Where Demonstrated in Demo | Source Code / Evidence Location | Verification Command |
| :--- | :--- | :--- | :--- |
| **1. Financial Risk Scoring** | Stream Table & Score Cards | `risk-service/RiskCalculationService.java` | `mvn test -pl risk-service` |
| **2. AI Behavioral Models** | Risk Score Calculation | `ai-engine/app/` | `curl http://localhost:8000/api/v1/ai/status` |
| **3. Relationship Graph** | SVG Topology Component | `FinancialRelationshipGraphComponent.ts` | Inspection in `/financial-risk` |
| **4. Cost-Aware Decisions** | Policy Simulator & Cost Curve | `FinancialRiskService.getBusinessCostSummary()` | `npm run build` in `frontend/sentinel` |
| **5. Explainable AI (XAI)** | Explainable AI Panel | `financial-risk.component.ts` (Lines 221–252) | Inspection in `/financial-risk` |
| **6. Human-in-the-Loop** | `REVIEW` State Banner | `financial-risk.component.ts` (Line 285) | Trigger Scenario B |
| **7. Incident Response** | Incident Workspace | `backend/alert-service/IncidentService.java` | `mvn test -pl alert-service` |
| **8. Threat Hunting** | Threat Hunting Route | `threat-hunting.component.ts` | Navigate to `/threat-hunting` |
| **9. Kafka Event Streaming** | Live Activity Log Stream | `risk-service/FinancialRiskEventConsumer.java` | Kafka Topic `sentinel.financial.events` |
| **10. Security & RBAC** | Gateway Auth Filter | `gateway-service/CorrelationIdFilter.java` | `mvn test -pl gateway-service` |
| **11. Model Benchmarking** | Model Comparison Table | `data/financial/evaluation/benchmark_summary.json` | `python -m unittest tests/test_phase6f_financial.py` |
