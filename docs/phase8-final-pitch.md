# Sentinel Phase 8 — 5-Minute Timed Presentation Pitch Script

**Track**: Razorpay AI Buildathon 2026 — Track 2 ("AI Risk Manager")  
**Project**: Sentinel — AI Financial Risk Manager  
**Target Duration**: Exactly 5 Minutes (00:00 - 05:00)  

---

## ⏱️ Pitch Timeline & Script Breakdown

### 0:00 – 0:30 | THE PROBLEM: Beyond Single-Transaction Blindness
> *"Good morning judges. In modern payment processing, a suspicious transaction is rarely suspicious by itself. When a fraudster attempts a transaction, they don't use high-velocity attacks that trigger basic rate limits. Instead, organized fraud rings split micro-transactions across synthetic accounts, sharing hardware device fingerprints, VPN subnet ranges, and payment instruments. Traditional transaction-level detectors evaluate transactions in isolation — missing distributed fraud rings entirely while accidentally blocking legitimate users sharing corporate IP subnets."*

### 0:30 – 1:00 | THE IDEA: Sentinel Financial Risk Intelligence
> *"To solve this, we built **Sentinel**. Sentinel is a relationship-aware, explainable, and cost-sensitive AI financial risk management platform. Sentinel doesn't look at a transaction in isolation. It combines transaction telemetry, behavioral signals, multi-hop entity graph relationships, machine learning inference, and asymmetric business cost optimization into an automated decision engine."*

### 1:00 – 1:45 | WHAT MAKES IT DIFFERENT: The 4-Layer Fusion
> *"What sets Sentinel apart is our 4-layer risk evaluation pipeline:  
> First, **Transaction & Behavioral Signals**: Multi-window velocity and account age.  
> Second, **Relationship Graph Intelligence**: An in-memory entity graph linking Users, Devices, IPs, and Payment References across 3 hops.  
> Third, **Explainable AI Inference**: Random Forest ensemble scoring calibrated probabilities.  
> Fourth, **Cost-Aware Policy Optimization**: Instead of arbitrary 50% cutoffs, Sentinel tunes operating boundaries ($\tau_{\text{block}}=0.50$, $\tau_{\text{review}}=0.25$) directly against real business costs — chargeback losses vs. customer drop-off friction."*

### 1:45 – 3:30 | LIVE DEMO: Scenarios A, B, C & Relationship Graph
> *(Switch to Live Angular SOC UI at `/financial-risk`)*  
> **Scenario A (Legitimate - APPROVE)**: *"Let's trigger Scenario A — a regular ₹2,450 purchase. Single device, single IP. Sentinel evaluates risk score **10 (LOW)** -> **APPROVE**."*  
> **Scenario B (Ambiguous - REVIEW)**: *"Next, Scenario B — a ₹14,500 purchase with 4 transactions in 1 hour across a shared IP subnet. Risk score **45 (MEDIUM)** -> **REVIEW**. Instead of blocking a potential corporate user, Sentinel routes this to a human analyst in our SOC workstation."*  
> **Scenario C (Fraud Ring - BLOCK)**: *"Finally, Scenario C — a high-risk ring master making a ₹48,500 transaction with 8 shared devices and 11 shared IP accounts. Risk score **100 (CRITICAL)** -> **BLOCK**. Instantly, Sentinel blocks the transaction, publishes a Kafka alert, creates an Incident record, and sends a Mailpit email alert!"*

### 3:30 – 4:15 | SOC INVESTIGATION: Incident Response & Threat Hunting
> *(Navigate to `/incidents` and `/threat-hunting`)*  
> *"The Sentinel platform doesn't stop at blocking transactions. It bridges risk detection directly into security operations. Here in the SOC Incident Workstation, analysts can review the timeline evidence, inspect graph node relationships, add investigation notes, and trigger threat hunting queries across historical entity clusters."*

### 4:15 – 4:40 | HELD-OUT BENCHMARK RESULTS & HONEST DISCLOSURES
> *"Our held-out benchmark evaluation on synthetic demonstration data achieved 100% recall on evasive fraud scenarios while eliminating false positives on corporate shared IP subnets. We disclose transparently that these reported benchmark numbers are on synthetic demonstration data designed to prove our graph pipeline implementation."*

### 4:40 – 5:00 | FINAL MESSAGE
> *"In summary, Sentinel does not only ask: IS THIS FRAUD?  
> It asks: HOW RISKY IS THIS? WHY? WHAT WILL IT COST THE BUSINESS? AND WHAT IS THE OPTIMAL OPERATIONAL ACTION?  
> Sentinel bridges relationship intelligence with business cost optimization. Thank you!"*
