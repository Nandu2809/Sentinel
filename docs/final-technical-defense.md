# Sentinel — Final Technical Deep Defense

## Overview
This document provides deep technical explanations for 20 architectural and implementation topics.

---

## 🔬 20 Technical Topics Deep Breakdown

### 1. System Microservices Architecture
- **What It Does**: 8 Spring Boot 3.3.5 microservices (`auth`, `gateway`, `monitoring`, `threat`, `risk`, `alert`, `report`) + 1 Python FastAPI ML engine.
- **Why It Exists**: Provides modular isolation, independent scaling, and clean domain separation.
- **Why This Design**: Allows high-concurrency event routing in Java while leveraging Python scientific ML ecosystems.
- **Limitation**: Inter-service HTTP calls require gateway orchestration; mitigated by Resilience4j circuit breakers.

### 2. Apache Kafka Event Streaming Bus
- **What It Does**: Manages asynchronous event streams over `sentinel.financial.events`, `sentinel.risk-events`, and `sentinel.alert-events`.
- **Why It Exists**: Decouples payment ingestion from downstream ML evaluation and SOC alerting.
- **Why This Design**: High throughput, fault-tolerant partition logs, and pub-sub stream semantics.
- **Limitation**: Requires Zookeeper coordination in local compose setup.

### 3. Java 21 & Spring Boot 3.3.5 Backend
- **What It Does**: Powers core business microservices, database JPA persistence, Flyway migrations, and REST controllers.
- **Why It Exists**: Enterprise-grade type safety, high concurrency, and Spring ecosystem integration.
- **Why This Design**: Virtual threads support high IOPS for incoming transaction telemetry.
- **Limitation**: Higher memory footprint than Go/Rust microservices.

### 4. Angular 17 Standalone Component Frontend
- **What It Does**: Renders SOC Command Center (`/financial-risk`), interactive SVG relationship graph, policy simulator, and incident workspace.
- **Why It Exists**: Delivers reactive, dark-mode cybersecurity UI with RxJS state management.
- **Why This Design**: Standalone component model provides fast build times and clear bundle splitting.
- **Limitation**: Bundle size requires lazy loading chunk management.

### 5. Python 3.12 FastAPI AI Engine
- **What It Does**: Serves Scikit-learn Random Forest and Isolation Forest anomaly models over REST endpoints.
- **Why It Exists**: Executes ML inference and outputs probability distributions.
- **Why This Design**: FastAPI provides lightweight, async ASGI Python server performance.
- **Limitation**: Single-process Python GIL requires multi-worker deployment for scale.

### 6. Financial Telemetry Feature Set (9 Baseline Features)
- **What It Does**: Extracts transaction amount, currency, merchant ID, account age, 1-hour velocity, 24-hour failed count.
- **Why It Exists**: Captures baseline transaction-level behavioral statistics.
- **Why This Design**: Standard quantitative signals used in payment fraud classification.
- **Limitation**: Misses multi-account collusion rings.

### 7. In-Memory Relationship Graph Topology Features (25 Enhanced Features)
- **What It Does**: Computes node degree, ring density, device sharing count, IP concentration, and payment token reuse.
- **Why It Exists**: Exposes multi-account fraud rings operating across shared infrastructure.
- **Why This Design**: Computed in-memory per Kafka event stream for sub-millisecond execution.
- **Limitation**: In-memory graph state requires persistent graph database (Memgraph/Neo4j) for long-term multi-month scaling.

### 8. Random Forest Classifier
- **What It Does**: Scores 34 feature vectors to predict risk probabilities ($0\text{--}100$).
- **Why It Exists**: Robust non-linear classification on tabular data.
- **Why This Design**: Resists overfitting and outputs feature importances for Explainable AI.
- **Limitation**: Static tree structure requires periodic offline re-training.

### 9. Multi-Factor Risk Scoring Engine
- **What It Does**: Fuses ML model probability outputs with rule-based heuristic factors into a unified score.
- **Why It Exists**: Provides continuous risk scoring for SOC prioritization.
- **Why This Design**: Maps $p \in [0, 1]$ directly to risk score $S \in [0, 100]$.
- **Limitation**: Score weighting requires periodic calibration against chargeback logs.

### 10. Asymmetric Business Cost Function $C_{\text{total}}(\tau)$
- **What It Does**: Formulates total expected business loss incorporating $C_{\text{FP}}$, $C_{\text{FN}}$, $C_{\text{REVIEW}}$, $C_{\text{FRICTION}}$.
- **Why It Exists**: Aligns model decision boundaries with financial business loss economics.
- **Why This Design**: Minimizes financial loss rather than maximizing raw accuracy.
- **Limitation**: Cost parameters are synthetic illustrative values in benchmark tests.

### 11. 3-Tier Policy Thresholds ($\tau_{\text{block}} = 0.50$, $\tau_{\text{review}} = 0.25$)
- **What It Does**: Categorizes events into `APPROVE`, `REVIEW`, or `BLOCK`.
- **Why It Exists**: Prevents binary block friction on ambiguous events.
- **Why This Design**: Optimal operating boundaries tuned on validation set.
- **Limitation**: Merchant-specific business models may require custom threshold tuning.

### 12. Human-in-the-Loop Review Buffer
- **What It Does**: Routes events ($0.25 \le p < 0.50$) to analyst `REVIEW` queue with visual indicator banner.
- **Why It Exists**: Prevents false blocks on high-value legitimate checkouts.
- **Why This Design**: Incurs small ₹400 investigation fee to protect customer relationships.
- **Limitation**: High transaction volumes require adequate analyst staffing.

### 13. SOC Incident Response Bridge
- **What It Does**: Automatically converts high-risk events into SOC alerts and active incidents (`INC-2026-1000`).
- **Why It Exists**: Eliminates silos between fraud analysts and security operations.
- **Why This Design**: Emits to `sentinel.alert-events` topic for real-time creation.
- **Limitation**: Incident creation rules must be tuned to prevent alert fatigue.

### 14. JWT Authentication & Gateway RBAC
- **What It Does**: Validates bearer tokens and enforces role-based access (`ROLE_USER`, `ROLE_ADMIN`).
- **Why It Exists**: Protects REST endpoints from unauthorized access.
- **Why This Design**: Gateway filter validates signature and checks Redis blacklist.
- **Limitation**: Local compose uses symmetric HMAC-SHA256 key fallback.

### 15. PostgreSQL Schema Isolation
- **What It Does**: Manages per-service relational schemas (`auth`, `monitoring`, `threat`, `risk`, `alert`, `report`).
- **Why It Exists**: Enforces microservice database boundary isolation.
- **Why This Design**: Flyway scripts automate schema migrations.
- **Limitation**: Single database instance in local compose setup.

### 16. Redis Token Bucket Rate Limiting & Cache
- **What It Does**: Enforces gateway rate limits and caches active sessions/blacklists.
- **Why It Exists**: Protects backend microservices from DDoS and brute force attacks.
- **Why This Design**: Sub-millisecond key-value lookups.
- **Limitation**: Requires memory eviction tuning under extreme load.

### 17. Temporal Leakage Prevention Protocol
- **What It Does**: Ensures historical aggregations compute windowed statistics over strictly past timestamps ($t < t_{\text{event}}$).
- **Why It Exists**: Prevents future event telemetry from leaking into training feature sets.
- **Why This Design**: Chronological sorting and rolling window computation.
- **Limitation**: Synthetic dataset timestamps are simulated.

### 18. Target Leakage Prevention Protocol
- **What It Does**: Excludes target labels (`isFraud`, `fraudScenario`) from feature input matrices.
- **Why It Exists**: Prevents models from cheating on training data.
- **Why This Design**: Strict feature manifest schema enforcement (`feature_manifest.json`).
- **Limitation**: Requires validation check during feature pipeline generation.

### 19. Train / Validation / Test Split Protocol
- **What It Does**: Splits dataset chronologically (70% Train, 10% Validation, 20% Held-Out Test).
- **Why It Exists**: Ensures honest model evaluation.
- **Why This Design**: Scaler fitted exclusively on Train; thresholds tuned on Validation.
- **Limitation**: Synthetic benchmark size capped at 10,000 records.

### 20. Synthetic Benchmark Dataset ($N=10,000$)
- **What It Does**: Provides 10,000 synthetic transaction records with 8 fraud scenarios ($8\%$ fraud rate).
- **Why It Exists**: Benchmarks model behavior safely without PII or real payment data.
- **Why This Design**: Controlled scenario generation for reproducible evaluation.
- **Limitation**: Benchmark separability yields 1.0 AUC; requires re-training for live deployment.
