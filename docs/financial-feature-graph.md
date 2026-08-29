# Sentinel Financial Feature & Relationship Graph Specification

## 1. Overview & Architecture
This document details the **Financial Feature & Relationship Graph Engine** implemented for **Sentinel Phase 6C** in alignment with **Razorpay Track 2 (AI Risk Manager)** research objectives.

The central research hypothesis evaluated by this architecture is:
> *"Does relationship information between users, devices, IPs, payment references, and merchants provide measurable fraud detection value beyond transaction-level features?"*

To evaluate this hypothesis without introducing infrastructural bloat or heavy database dependencies, Phase 6C implements an **In-Memory Temporal Entity Relationship Graph** in Python.

---

## 2. Graph Model & Schema

### Entity Types (Nodes)
- **`USER`**: Account user identifier (`userId`)
- **`DEVICE`**: Hardware device fingerprint token (`deviceId`)
- **`IP`**: Network IPv4 address (`ipAddress`)
- **`PAYMENT_METHOD`**: Tokenized payment reference (`paymentMethodRef`)
- **`MERCHANT`**: Merchant account identifier (`merchantId`)

### Relationship Types (Edges)
- `USER --USES--> DEVICE`
- `USER --CONNECTS_FROM--> IP`
- `USER --USES--> PAYMENT_METHOD`
- `USER --PAYS--> MERCHANT`
- `DEVICE --SEEN_FROM--> IP`
- `PAYMENT_METHOD --USED_AT--> MERCHANT`

---

## 3. Temporal Window & Anti-Leakage Methodology

### Strict Temporal Correctness
To prevent future-data leakage in offline evaluation:
1. All transactions are processed in strict chronological timestamp order.
2. For transaction $T$ occurring at time $t$, relationship features are calculated using graph state accumulated **at or prior to** time $t$.
3. Future transactions $T_{\text{future}} > t$ are strictly invisible to the graph during feature extraction for $T$.

### Sliding Windows
- **1-Hour Window ($t - 1\text{h} \le \text{timestamp} \le t$)**: Captures rapid high-velocity abuse signals (e.g. `recentSharedDeviceCount`, `transactionVelocityOnSharedIp`).
- **24-Hour Window ($t - 24\text{h} \le \text{timestamp} \le t$)**: Captures broader infrastructure reuse across days (e.g. `recentPaymentReuseCount`).

---

## 4. Feature Definitions: Baseline vs Enhanced

### A. BASELINE FEATURES (Transaction-Level Telemetry Only)
- `amount`: Raw transaction amount in INR.
- `logAmount`: Log1p transformed transaction amount.
- `accountAgeDays`: User account age in days at transaction time.
- `velocity1h`: Transactions attempted by user in prior 1 hour.
- `failedTxCount24h`: Failed transaction attempts in prior 24 hours.
- `sharedDeviceAccountCount`: Event-level device account count.
- `sharedIpAccountCount`: Event-level IP account count.
- `isNewAccountFlag`: Binary flag indicating `accountAgeDays <= 7`.
- `highVelocityFlag`: Binary flag indicating `velocity1h >= 5`.

### B. ENHANCED FEATURES (Baseline + Relationship Graph Metrics)
- `sharedPaymentAccountCount`: Distinct users linked to payment reference.
- `deviceDegree`, `ipDegree`, `paymentMethodDegree`, `merchantDegree`: Entity graph degree centrality.
- `uniqueDevicesPerUser`, `uniqueIpsPerUser`, `uniquePaymentMethodsPerUser`: Per-user entity counts.
- `accountsPerDevice`, `accountsPerIp`, `accountsPerPaymentMethod`: Cumulative entity sharing metrics.
- `recentSharedDeviceCount`, `recentSharedIpCount` (1h window): Sliding window shared entity counts.
- `recentPaymentReuseCount` (24h window): Sliding window payment reuse count.
- `transactionVelocityOnSharedDevice`, `transactionVelocityOnSharedIp`: Total velocity across all accounts on shared node.
- `deviceClusterSize`, `ipClusterSize`, `paymentClusterSize`: 2-hop graph neighborhood node counts.
- `multiAccountDeviceFlag`, `multiAccountIpFlag`, `multiAccountPaymentFlag`: Multi-account entity sharing flags.
- `sharedInfrastructureScore`: Composite normalized infrastructure risk score.
- `relationshipDensity`: Local 2-hop sub-graph edge density.
- `ringConnectivityScore`: Log1p product of shared device and IP account counts.

---

## 5. Feature Manifest & Output Datasets

The pipeline generates three artifacts in `data/financial/`:
1. `features_baseline.csv`: 10,000 records containing `transactionId`, `isFraud`, and 9 baseline predictive features.
2. `features_enhanced.csv`: 10,000 records containing `transactionId`, `isFraud`, and 34 baseline + graph enhanced predictive features.
3. `feature_manifest.json`: Machine-readable specification listing feature categories, types, descriptions, and confirming `uses_future_data: false`.

---

## 6. Leakage Prevention Controls

1. **Target Label Leakage**: Ground-truth target labels (`isFraud`, `fraudScenario`) are strictly excluded from predictive feature vectors.
2. **Temporal Leakage**: Features are calculated strictly backward-looking in time.
3. **No Trivial Rules**: Features represent topological and velocity metrics only. No rule hardcodes `if sharedDeviceAccountCount > X: fraud = true`.

---

## 7. Computational Complexity & Performance

- **Graph Model**: In-memory Python adjacency list structure using `defaultdict(set)` and timed event logs.
- **Extraction Speed**: ~4,647 records/sec on synthetic benchmark (10,000 records processed in ~2.15 seconds).
- **Space Complexity**: $O(|V| + |E|)$ scaling linearly with transaction count.

---

## 8. Research Prototype Limitations & Disclaimers

> **RESEARCH DISCLAIMER**:
> *This graph engine is a research prototype designed for offline experimental evaluation on synthetic data.*
> *It does NOT claim to represent a production-scale distributed payment graph (such as Neo4j or TigerGraph).*
> *Phase 6C does NOT claim that graph features improve fraud detection accuracy; it builds the feature extraction infrastructure required to scientifically measure that hypothesis in Phase 6D/6E.*
