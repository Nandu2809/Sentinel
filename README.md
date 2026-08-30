# Sentinel — AI Risk Manager & Security Intelligence Platform

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Track](https://img.shields.io/badge/Razorpay%20Hackathon-Track%202%20AI%20Risk%20Manager-blueviolet)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)
![Python](https://img.shields.io/badge/Python-3.12-blue)
![Angular](https://img.shields.io/badge/Angular-17-red)
![License](https://img.shields.io/badge/license-MIT-blue)

**Sentinel** is an explainable, relationship-aware, cost-sensitive **AI Risk Management Platform** that converts financial transaction telemetry into operational security decisions. Built for the **Razorpay Hackathon Track 2 ("AI Risk Manager")**, Sentinel elevates fraud management beyond binary classification by fusing in-memory entity graph topology, behavioral machine learning, asymmetric business cost optimization, and SOC incident response.

---

## 💡 Why Sentinel is Different

> *"Sentinel does not only ask: IS THIS FRAUD? It asks: HOW RISKY IS THIS? WHY? WHAT WILL IT COST? AND WHAT SHOULD WE DO?"*

1. **Relationship-Aware Intelligence**: 25 in-memory topology features linking Users, Hardware Device Fingerprints, IP Subnets, Payment Token References, and Merchants into multi-hop entity clusters.
2. **Asymmetric Business Cost Optimization**: Decision operating boundaries ($\tau_{\text{block}} = 0.50$, $\tau_{\text{review}} = 0.25$) tuned against false positive penalties ($C_{\text{FP}} = \text{₹1,200}$), false negative chargebacks ($C_{\text{FN}} = \text{₹6,800}$), analyst review costs ($C_{\text{REVIEW}} = \text{₹400}$), and customer friction ($C_{\text{FRICTION}} = \text{₹160}$).
3. **3-Tier Decision Engine**: Automated `APPROVE` ($p < 0.25$), human-in-the-loop analyst `REVIEW` ($0.25 \le p < 0.50$), and automated `BLOCK` ($p \ge 0.50$).
4. **Anti-Leakage Scientific Discipline**: Scaler fit exclusively on `train.csv`, operating thresholds tuned on `validation.csv`, and held-out test data evaluated strictly once. Zero target or temporal leakage.
5. **Integrated SOC Command Center**: Real-time Angular 17 Cyber Intelligence workstation (`/financial-risk`) with SVG topology graph, threshold policy simulator, business cost curve, and direct bridges to Incident Response (`/incidents`) and Threat Hunting (`/threat-hunting`).

---

## 🏗️ End-to-End System Architecture

```
[Financial Transaction Telemetry]
                │
                ▼
  FinancialRiskEvent (Envelope)
                │
                ▼
  Kafka: sentinel.financial.events
                │
                ▼
  FinancialRiskEventConsumer (risk-service)
                │
     ┌──────────┴──────────┐
     ▼                     ▼
Feature Extraction   Topology Graph Engine
 (34 Features)       (Multi-Hop Entity Rings)
     │                     │
     └──────────┬──────────┘
                ▼
     Baseline & Enhanced ML
                │
                ▼
    Cost-Aware Decision Engine
 (APPROVE / REVIEW / BLOCK Policy)
                │
     ┌──────────┴──────────┐
     ▼                     ▼
PostgreSQL        Kafka: sentinel.alert-events
(Risk DB)                  │
                           ▼
                  Alert & Incident Services
                           │
                           ▼
               Sentinel SOC Command Center
                    (/financial-risk)
```

---

## 📊 Phase 6F Benchmark Results

| Model Architecture | Feature Set | Precision | Recall | F1-Score | ROC-AUC | FP | FN | Expected Cost |
| :--- | :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Baseline Random Forest** | Baseline (9 Features) | `0.9925` | `1.0000` | `0.9962` | `1.0000` | `1` | `0` | ₹1,200 |
| **Enhanced Graph RF** | Enhanced (34 Features) | `0.9565` | `1.0000` | `0.9778` | `1.0000` | `6` | `0` | ₹7,200 |

### Key Scenario Discoveries
- **`EVASIVE_FRAUD` Gain**: Graph intelligence improved recall from **$95.65\% \rightarrow 100.0\%$ (+4.35%)**.
- **`LEGITIMATE_SHARED_INFRASTRUCTURE`**: Graph topology reduced false positives on corporate NAT subnets from **$6 \rightarrow 0$ FP**.
- **`LOW_AND_SLOW_RING` Tradeoff**: Disclosed honest limitation where baseline features caught low-velocity fraud ($100\% \rightarrow 42.31\%$).

---

## 🛠️ Technology Stack

- **Backend**: Java 21, Spring Boot 3.3.5, Spring Cloud Gateway, Spring Security, Spring Data JPA, Flyway, Micrometer, OpenAPI.
- **AI Engine**: Python 3.12, PyTorch, Scikit-learn, Pandas, NumPy, Joblib.
- **Databases & Event Bus**: PostgreSQL 16, Redis 7, Apache Kafka & Zookeeper.
- **Frontend UI**: Angular 17, Tailwind CSS, RxJS, SVG Graph Engine.

---

## 🚀 Quick Start

```bash
# 1. Clone Repository
git clone https://github.com/madugundunanda-ui/Sentinel.git
cd Sentinel

# 2. Run Backend Unit Tests
cd backend && mvn test

# 3. Build Angular SOC UI
cd ../frontend/sentinel && npm run build

# 4. Launch Microservices & Infrastructure
cd ../../infrastructure/docker && docker compose up -d
```

---

## 📖 Key Hackathon Documentation

- 🎯 [Judge Evidence & Capabilities](docs/judge-evidence.md)
- 🎬 [5-Minute Judge Demo Guide](docs/hackathon-demo.md)
- 📐 [Final System Architecture Audit](docs/phase6h-final-architecture-audit.md)
- 📊 [Model Robustness & Validity Analysis](docs/phase6h-model-validity.md)
- 🔬 [Phase 6H Final Verification Matrix](docs/phase6h-final-verification.md)
- 💰 [Cost Benchmark & Decision Optimization](docs/financial-risk-cost-benchmark.md)
- 🕸️ [Relationship Graph Feature Pipeline](docs/financial-feature-graph.md)

---

## 📜 License
Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.
