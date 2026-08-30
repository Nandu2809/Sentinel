# Sentinel — Core Product Story

## Executive Summary
This document answers the 10 core product and business questions defining Sentinel's value proposition for Razorpay Hackathon Track 2 (*AI Risk Manager*).

---

## 10 Core Product Answers

### 1. What problem does Sentinel solve?
Sentinel solves financial loss, customer churn, and security operational blindness caused by rigid rule engines and simple binary ML fraud classifiers in payment ecosystems.

### 2. Who is the user?
Primary users are **Payment Risk Operations Teams**, **Fraud Analysts**, and **Security Operations Center (SOC) Teams** managing high-throughput transaction flows for payment gateways and merchants.

### 3. Why is simple fraud classification insufficient?
Binary fraud classifiers output only `0` or `1` (or a raw probability) without explaining *why* a transaction was flagged, without considering the financial cost of a false positive vs. a false negative, and without providing an operational pathway to investigate or resolve the threat.

### 4. Why does relationship intelligence matter?
Modern fraudsters do not operate in isolation. They execute evasive fraud using shared device pools, proxy IP subnets, and compromised payment token references. Relationship intelligence reveals multi-hop graph rings that transaction-level features cannot detect.

### 5. Why is AI needed?
Behavioral patterns evolve faster than manual rules can be updated. Machine learning models analyze 34+ high-dimensional features (velocity, account age, spatial distance, graph density) to detect non-linear anomaly patterns in real-time.

### 6. Why is graph intelligence needed?
Graph topology features measure graph density, node degree, ring connectivity, and entity sharing. This prevents false positives on shared infrastructure (e.g., corporate NAT gateways) while detecting distributed botnet checkout rings.

### 7. Why is cost-aware decision making needed?
Not all classification errors carry equal weight. A false block ($C_{\text{FP}} = \text{₹1,200}$) costs revenue and customer goodwill. A missed fraud ($C_{\text{FN}} = \text{₹6,800}$) causes direct chargeback loss. Cost-aware decision making sets operating boundaries ($\tau_{\text{block}} = 0.50$, $\tau_{\text{review}} = 0.25$) to minimize expected business loss.

### 8. Why should some transactions go to REVIEW instead of BLOCK?
Ambiguous transactions (e.g., first-time high-value purchases on corporate networks) fall in an intermediate risk zone ($0.25 \le p < 0.50$). Routing them to analyst `REVIEW` ($C_{\text{REVIEW}} = \text{₹400}$) avoids insulting legitimate high-value customers while protecting against potential fraud.

### 9. What happens after a BLOCK?
When a transaction is blocked ($p \ge 0.50$), Sentinel records the risk entity in PostgreSQL, emits a real-time event to `sentinel.alert-events`, creates a tracking incident in `/incidents`, and provides one-click navigation for SOC analysts to inspect the entity ring in `/threat-hunting`.

### 10. How does Sentinel help a security/risk team?
Sentinel unifies fraud risk scoring with SOC incident workflows. It reduces alert fatigue, provides clear Explainable AI attributions, allows offline policy threshold simulation, and automates incident lifecycle management.
