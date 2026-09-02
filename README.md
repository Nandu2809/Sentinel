# Sentinel — Relationship-Aware AI Financial Risk Intelligence Platform

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)
![Python](https://img.shields.io/badge/Python-3.12-blue)
![Angular](https://img.shields.io/badge/Angular-17-red)
![License](https://img.shields.io/badge/license-MIT-blue)

> **Important Disclosure**: *Sentinel is a prototype financial risk intelligence platform. All benchmarks and performance evaluations reported in this documentation were performed using synthetic demonstration data and engineered risk scenarios. These metrics serve as empirical evidence of the implemented pipeline and should not be interpreted as production accuracy on real-world payment traffic.*

---

## 🎯 One-Line Description
Sentinel is a relationship-aware, explainable, and cost-sensitive **AI Financial Risk Intelligence Platform** that extends financial transaction monitoring beyond binary isolation by combining multi-hop entity relationship intelligence, behavioral machine learning, asymmetric business-cost optimization, and SOC incident response.

---

## 💥 The Problem
In modern digital payments, **a suspicious transaction is rarely suspicious by itself**. 

Traditional transaction-level fraud detectors evaluate transactions in isolation using static velocity rules or standard classifiers. Sophisticated financial fraud rings exploit this blind spot by spreading low-velocity, micro-amount transactions across distributed entities:
- **Shared Hardware Devices**: Multiple accounts operating from identical browser/device fingerprints.
- **Shared IP Subnets & Proxies**: Fraud syndicates hiding behind corporate NATs or residential VPN ranges.
- **Payment Instrument Reuse**: Compromised credit card numbers or UPI handles reused across synthetic accounts.
- **Velocity Evasion**: Ring members remaining under standard 24-hour frequency thresholds.

When fraud detectors look at transactions independently, organized fraud rings evade detection while legitimate users sharing corporate IP subnets get wrongly blocked (false positives).

---

## 💡 Why This Problem Matters
- **Direct Financial Losses**: Chargebacks, stolen funds, and merchant fines ($C_{\text{FN}} \approx \text{₹6,800}$ per undetected incident).
- **Customer Churn & Friction**: Legitimate customers blocked during high-value checkouts ($C_{\text{FP}} \approx \text{₹1,200}$ per false positive).
- **Analyst Operational Fatigue**: SOC teams overwhelmed by noisy, unprioritized alerts lacking explainability ($C_{\text{REVIEW}} \approx \text{₹400}$ per manual audit).

---

## 🛡️ The Solution: Sentinel
Sentinel introduces a 4-dimensional risk management framework:
1. **Transaction Intelligence**: Real-time evaluation of payment amount, currency, merchant category, and timing.
2. **Behavioral Intelligence**: Account age, historical velocity, and failure rates.
3. **Relationship Graph Intelligence**: In-memory entity graph linking Users, Devices, IPs, Payment References, and Merchants into multi-hop entity rings.
4. **Asymmetric Cost Decision Engine**: Optimizes decision operating boundaries ($\tau_{\text{block}} = 0.50$, $\tau_{\text{review}} = 0.25$) against real business costs rather than symmetric accuracy.

---

## 🚀 What Makes Sentinel Different

| Capability | Traditional Rules | Standalone ML | Graph-Only | SOC Platforms | **Sentinel Platform** |
|---|:---:|:---:|:---:|:---:|:---:|
| Transaction Signal Analysis | ✅ | ✅ | ❌ | ❌ | **✅ Advanced** |
| Behavioral Velocity | Static | Dynamic | ❌ | ❌ | **✅ Multi-Window** |
| Multi-Hop Relationship Topology | ❌ | ❌ | ✅ | ❌ | **✅ In-Memory Graph** |
| Asymmetric Cost Optimization | ❌ | Symmetrically Tuned | ❌ | ❌ | **✅ Cost-Aware Policy** |
| Explainable Risk Factor Attribution | ❌ | Feature Weights Only | ❌ | ❌ | **✅ Graph + ML SHAP** |
| 3-Tier Policy (`APPROVE` / `REVIEW` / `BLOCK`) | ❌ | Binary | Binary | ❌ | **✅ Human-in-the-Loop** |
| SOC Alert & Incident Escalation | ❌ | ❌ | ❌ | ✅ | **✅ Native Incident Pipeline** |
| Threat Hunting Workstation | ❌ | ❌ | ❌ | Partial | **✅ Pattern Query Engine** |

---

## 🏗️ System Architecture

```
                         ┌──────────────────┐
                         │  Angular Frontend │
                         │ SOC Workstation UI│
                         └─────────┬────────┘
                                   │
                                   ▼
                         ┌──────────────────┐
                         │    API Gateway   │
                         │      :8088       │
                         └─────────┬────────┘
                                   │
          ┌────────────────────────┼────────────────────────┐
          │                        │                        │
          ▼                        ▼                        ▼
    Auth Service            Financial Risk            SOC Services
       :8081                    :8084              Alert / Incident
                                  │
                                  ▼
                         ┌──────────────────┐
                         │    AI Engine     │
                         │      :8000       │
                         └─────────┬────────┘
                                   │
                                   ▼
                         ┌──────────────────┐
                         │ Relationship     │
                         │ Graph Intelligence│
                         └─────────┬────────┘
                                   │
                                   ▼
                         ┌──────────────────┐
                         │ Cost-Aware Risk  │
                         │ Decision Engine  │
                         └─────┬────┬───────┘
                               │    │
                         APPROVE│    │BLOCK
                               │    │
                               ▼    ▼
                            REVIEW ALERT
                                   │
                                   ▼
                               INCIDENT
                                   │
                                   ▼
                            THREAT HUNTING
```

---

## 💳 Financial Risk Pipeline

1. **Transaction Telemetry Ingestion**: Incoming `FinancialRiskEvent` received via REST / Kafka (`sentinel.financial.events`).
2. **Feature Extraction (34 Features)**: Computes 9 baseline transaction features + 25 graph topology features.
3. **Graph Topology Traversal**: Computes degree centrality, device-sharing ratio, IP fan-out, and payment token reuse across 1-hop, 2-hop, and 3-hop entity clusters.
4. **Ensemble ML Evaluation**: Evaluates Random Forest classifier to produce calibrated fraud probability $p \in [0.0, 1.0]$.
5. **Cost-Aware Policy Decisioning**:
   - **`APPROVE`** ($p < 0.25$): Low risk. Transaction completes seamlessly.
   - **`REVIEW`** ($0.25 \le p < 0.50$): Medium risk. Escalates to Human-in-the-Loop SOC analyst.
   - **`BLOCK`** ($p \ge 0.50$): High risk. Automated block + Kafka event (`sentinel.alert-events`) + Alert + Incident + Email.

---

## 🕸️ Relationship Graph Intelligence
Sentinel maintains an in-memory entity graph linking five canonical node types:
- `USER`
- `DEVICE`
- `IP_ADDRESS`
- `PAYMENT_REF`
- `MERCHANT`

### Calculated Graph Signals:
- `sharedDeviceAccountCount`: Number of unique accounts bound to the exact same device footprint.
- `sharedIpAccountCount`: Number of distinct user accounts operating through the IP.
- `paymentRefReuseCount`: Degree of payment instrument sharing across accounts.
- `ringConnectivityScore`: Graph density and cluster coefficient of the connected component.

---

## ⚖️ Cost-Aware Decisioning
Sentinel optimizes operating thresholds against the expected business cost formula:
$$\mathbb{E}[\text{Cost}] = C_{\text{FP}} \cdot N_{\text{FP}} + C_{\text{FN}} \cdot N_{\text{FN}} + C_{\text{REVIEW}} \cdot N_{\text{REVIEW}} + C_{\text{FRICTION}} \cdot N_{\text{FRICTION}}$$

### Cost Parameters:
- $C_{\text{FN}} = \text{₹6,800}$ (Chargeback loss + merchant fine)
- $C_{\text{FP}} = \text{₹1,200}$ (Customer drop-off loss)
- $C_{\text{REVIEW}} = \text{₹400}$ (Analyst labor cost)
- $C_{\text{FRICTION}} = \text{₹160}$ (Stepped-up authentication friction)

---

## 📊 Benchmark Results (Phase 6F Hardened Dataset)

> **Dataset Split**: 1,000 Total Transactions (920 Legitimate, 80 Fraudulent — 8.0% Base Ratio).  
> **Train**: 700 rows | **Val**: 100 rows | **Held-Out Test**: 200 rows (18 Fraudulent).

| Model Architecture | Feature Set | Precision | Recall | F1-Score | ROC-AUC | FP | FN | Expected Cost |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Baseline Model** | Baseline (9 Features) | `0.9925` | `1.0000` | `0.9962` | `1.0000` | `1` | `0` | ₹1,200 |
| **Enhanced Graph Model** | Enhanced (34 Features) | `0.9565` | `1.0000` | `0.9778` | `1.0000` | `6` | `0` | ₹7,200 |

### Scientific Limitation Disclosure:
- **Synthetic Demonstration Dataset**: The dataset contains synthetic fraud ring patterns explicitly generated to demonstrate multi-hop graph detection capabilities.
- **Near-Perfect AUC**: High ROC-AUC values reflect clean boundary separability in controlled synthetic scenarios and do not imply identical performance on noisy production payment streams.

---

## 🛠️ Technology Stack

- **Backend Microservices**: Java 21, Spring Boot 3.3.5, Spring Cloud Gateway, Spring Security JWT, Spring Data JPA, Flyway, Micrometer.
- **AI & ML Engine**: Python 3.12, Scikit-learn, Pandas, NumPy, Joblib.
- **Databases & Messaging**: PostgreSQL 16, Redis 7, Apache Kafka & Zookeeper.
- **Frontend Workstation**: Angular 17, Tailwind CSS, RxJS, SVG Graph Visualization Engine.
- **Email Testing**: Mailpit.

---

## ⚡ Running Sentinel

### Quick Local Start:
```bash
# 1. Clone & Navigate
git clone https://github.com/madugundunanda-ui/Sentinel.git
cd Sentinel

# 2. Execute Backend Tests
mvn clean test

# 3. Build Angular Frontend
cd frontend/sentinel && npm run build

# 4. Start Infrastructure Stack
cd ../../infrastructure/docker && docker compose up -d
```

---

## 📖 Project Documentation Index
- 📐 [System Architecture & Data Flow](docs/phase8-architecture.md)
- 🎬 [Presentation Pitch Script](docs/phase8-final-pitch.md)
- ❓ [Risk Intelligence Q&A Master Sheet](docs/phase8-judge-qna.md)
- 📊 [Benchmark Evidence & Disclosures](docs/phase8-benchmark-evidence.md)
- ⚖️ [Competitive Positioning & Feature Matrix](docs/phase8-competitive-positioning.md)
- 🧪 [Final Maven Test Report](docs/phase8-final-test-report.md)
- 💻 [Frontend Verification Report](docs/phase8-frontend-verification.md)
- 📋 [Demo Screenshot Checklist](docs/phase8-screenshot-checklist.md)
- 🔄 [Demo Rehearsal Run Logs](docs/phase8-demo-rehearsal.md)
- 🚨 [Emergency Component Fallback Plan](docs/phase8-demo-fallback.md)
- ✅ [System Release Checklist](docs/phase8-submission-checklist.md)
- 💻 [Demo Day Command Sheet](docs/phase8-command-sheet.md)
- 🏆 [Final Readiness Scorecard](docs/phase8-final-readiness.md)
- 💎 [Value Proposition Document](docs/phase8-value-proposition.md)

---

## 📜 License
Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.
