# Sentinel Phase 8 — Demo Screenshot Presentation Checklist

This checklist documents the exact 14 UI screens, views, and architectural diagrams required for judge presentation slides and repository documentation.

---

## 📸 Presentation Screen Checklist

| # | Screen Name | Route / Path | Purpose & Focus Area | What the Judge Should Notice |
|---|---|---|---|---|
| 1 | **Login Screen** | `/login` | JWT Authentication Entry Point | Clean dark-mode UI, JWT token authentication, secure role access |
| 2 | **Executive Dashboard** | `/dashboard` | Overall Security Overview | Real-time risk cards, active alerts summary, system health matrix |
| 3 | **Financial Risk Workstation** | `/financial-risk` | Core Financial Risk Command Center | Interactive SVG relationship graph, decision logs, activity stream |
| 4 | **Scenario A: Legitimate** | `/financial-risk` | Legitimate Transaction Demo | Risk Score: **10 (LOW)**, Decision: **APPROVE**, single device/IP node |
| 5 | **Scenario B: Ambiguous** | `/financial-risk` | Human-in-the-Loop Review Demo | Risk Score: **45 (MEDIUM)**, Decision: **REVIEW**, corporate shared IP |
| 6 | **Scenario C: Fraud Ring** | `/financial-risk` | Automated Block & Escalation Demo | Risk Score: **100 (CRITICAL)**, Decision: **BLOCK**, multi-device ring |
| 7 | **Relationship Graph** | `/financial-risk` | SVG Graph Visualization | Multi-hop nodes (`USER`, `DEVICE`, `IP`, `PAYMENT_REF`), ring edges |
| 8 | **Risk Explainability** | `/financial-risk` | SHAP & Risk Factor Attribution | Explicit risk factor cards (`HIGH_DEVICE_SHARING`, `SHARED_IP_BURST`) |
| 9 | **Cost Curve & Simulator** | `/financial-risk` | Cost-Aware Policy Simulator | Dynamic operating threshold slider, expected cost minimization curve |
| 10 | **SOC Incident Detail** | `/incidents/:id` | Incident Investigation Workstation | Incident number (`INC-2026-XXXX`), evidence timeline, analyst notes |
| 11 | **Threat Hunting** | `/threat-hunting` | Security Intelligence Workstation | Pattern query builder, shared device ring search results, node drilldown |
| 12 | **Mailpit Alerts** | `:8025` | Asynchronous Email Delivery | Instant HTML email notifications dispatched on `BLOCK` decision |
| 13 | **Benchmark Report** | `docs/phase8-benchmark-evidence.md` | Model Evaluation & Disclosures | Train/Val/Test splits, precision/recall table, synthetic data disclosure |
| 14 | **Architecture Diagram** | `docs/phase8-architecture.md` | System Microservice Topology | Microservice topology, Kafka event pipeline, 11-stage evaluation flow |

---

## 📌 Rules for Screenshot Capture
- Capture screenshots strictly from the live running Angular application on `http://localhost:4200`.
- Do not modify or fake UI values.
- Ensure all screenshots showcase dark-mode glassmorphism styling and responsive layout.
