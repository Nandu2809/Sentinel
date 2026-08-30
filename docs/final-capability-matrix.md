# Sentinel — Final Capability Matrix

## Matrix Overview
This document consolidates all platform capabilities across backend, AI, frontend, and infrastructure.

---

## Final Capability Matrix

| Capability | Implemented? | Location in Codebase | Verification Method | Demo Importance | Known Limitation |
| :--- | :---: | :--- | :--- | :---: | :--- |
| **Authentication & RBAC** | **YES** | `backend/auth-service` | `mvn test` in auth-service | **HIGH** | Uses symmetric JWT in dev setup. |
| **Kafka Event Ingestion** | **YES** | `risk-service/FinancialRiskEventConsumer` | Topic `sentinel.financial.events` audit | **CRITICAL** | Requires Kafka container running. |
| **Threat Detection Engine** | **YES** | `backend/threat-service` | OWASP detectors test suite | **MEDIUM** | Rule set optimized for web APIs. |
| **AI Behavioral Analysis** | **YES** | `ai-engine/` | `/api/v1/ai/status` endpoint | **HIGH** | Model trained on synthetic data. |
| **Financial Risk Scoring** | **YES** | `risk-service/RiskCalculationService` | `FinancialRiskControllerTest` | **CRITICAL** | Scored 0–100 with probability mapping. |
| **Relationship Graph** | **YES** | `FinancialRelationshipGraphComponent` | Interactive SVG component | **CRITICAL** | Graph built in-memory per event. |
| **Cost-Aware Decisions** | **YES** | `RiskCalculationService` | Phase 6E business cost benchmark | **CRITICAL** | Costs labeled as illustrative. |
| **Explainable AI (XAI)** | **YES** | `financial-risk.component.ts` | "Why This Decision?" panel | **HIGH** | 4 primary attributions displayed. |
| **Human-in-the-Loop** | **YES** | `financial-risk.component.ts` | `REVIEW` state banner | **HIGH** | Interactive analyst indicator. |
| **Alert Management** | **YES** | `backend/alert-service` | `EscalationEngineTest` | **HIGH** | Emits to `sentinel.alert-events`. |
| **Incident Response** | **YES** | `backend/alert-service` | `IncidentServiceTest` | **HIGH** | Maps threatId to INC-2026-1000. |
| **Threat Hunting** | **YES** | `threat-hunting.component.ts` | Navigation query params | **MEDIUM** | Filterable by IP/user/device. |
| **Frontend SOC Command** | **YES** | `frontend/sentinel` | `npm run build` | **CRITICAL** | Standalone Angular 17 workspace. |
| **Audit Logging** | **YES** | `FinancialRiskService` | Activity console feed | **MEDIUM** | Preserved in component state. |
| **Mailpit Notification** | **YES** | `docker-compose.yml` | Mailpit container (port 1025) | **LOW** | Emits email on SLA breach. |
| **Benchmark Evaluation** | **YES** | `data/financial/evaluation/` | `benchmark_summary.json` | **HIGH** | Verified Phase 6F metrics. |
