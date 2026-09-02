# Sentinel Phase 7 — 5-Minute Timed Judge Demonstration Script

**Track 2:** AI Risk Manager  
**Platform:** Sentinel — AI Financial Risk Manager  
**Target Duration:** Exactly 5 Minutes (05:00)

---

## Timed Presentation Sequence

### 00:00–00:30 — THE PROBLEM
**Speaker:**
> *"Traditional payment fraud detection systems evaluate transactions in isolation. But modern fraud attacks are rarely isolated — coordinated payment rings reuse the same devices, IP ranges, and payment instruments across dozens of synthetic accounts. Sentinel evaluates transactions, entity relationships, behavioral AI signals, and business cost together."*

---

### 00:30–01:15 — LEGITIMATE TRANSACTION (SCENARIO A)
**Action:** Click **SCENARIO A: LEGITIMATE** in the Judge Mode banner on `http://localhost:4200/financial-risk`.

**UI Displays:**
- Risk Score: **10 / 100** (LOW)
- Decision: **APPROVE**
- Business Cost: **₹0**

**Speaker:**
> *"Here is a standard user transaction. Even if the user connects from a shared office network, Sentinel recognizes nominal behavioral patterns and instantly APPROVES the transaction without creating friction for legitimate customers."*

---

### 01:15–02:00 — AMBIGUOUS TRANSACTION & HUMAN-IN-THE-LOOP (SCENARIO B)
**Action:** Click **SCENARIO B: AMBIGUOUS** in the Judge Mode banner.

**UI Displays:**
- Risk Score: **45–54 / 100** (MEDIUM)
- Decision: **REVIEW**
- Analyst Queue: Assigned for Human-in-the-Loop review.

**Speaker:**
> *"Here is an ambiguous transaction — a user making a higher-value purchase from a corporate IP with slight velocity acceleration. Rather than blocking the user or letting a potential loss pass, Sentinel flags the event for Human-in-the-loop analyst review, balancing safety with customer retention."*

---

### 02:00–03:15 — COORDINATED FRAUD RING ATTACK (SCENARIO C)
**Action:** Click **SCENARIO C: HIGH-RISK RING** in the Judge Mode banner.

**UI Displays:**
- Risk Score: **100 / 100** (CRITICAL)
- Decision: **BLOCK**
- Relationship Topology Graph: Highlights connected Device Cluster (8 accounts) and IP Subnet (11 accounts).

**Speaker:**
> *"Now we trigger a coordinated fraud ring attack. Instantly, Sentinel's Relationship Graph detects an active cluster: 8 accounts sharing one device, 11 accounts sharing an IP, reusing card tokens. The decision engine issues an automated BLOCK decision, emits a Kafka event, generates a high-severity security alert, and dispatches a Mailpit notification."*

---

### 03:15–04:00 — EXPLAINABLE AI & COST-AWARE RISK POLICY
**Action:** Focus on the **WHY THIS DECISION?** panel and **Business Cost Curve**.

**UI Displays:**
- Observed Risk Factors: *Shared Payment Ring Reference*, *Device linked to 8 accounts*.
- Cost Matrix: Shows False Positive Cost vs. False Negative Cost vs. Customer Friction.

**Speaker:**
> *"Sentinel provides complete explainability. We don't just output a black-box score — we explain the exact signals triggering the risk and evaluate the business cost of being wrong."*

---

### 04:00–04:30 — SOC INCIDENT RESPONSE & THREAT HUNTING
**Action:** Click **🚨 OPEN INCIDENT WORKSPACE (/incidents)** -> Navigate to `/threat-hunting`.

**UI Displays:**
- Incident Number & Investigation Timeline.
- Analyst Notes & Actions.
- Threat Hunting bridge searching by IP `198.51.100.44`.

**Speaker:**
> *"Security operations teams can investigate the blocked transaction in real time, audit evidence timelines, execute mitigation actions, and pivot directly into Threat Hunting to discover related malicious accounts."*

---

### 04:30–05:00 — COMPETITIVE DIFFERENTIATION & CLOSING
**Speaker:**
> *"Sentinel doesn't look at only the transaction. It looks at the transaction, the relationships around it, the behavior behind it, and the business cost of getting the decision wrong. Thank you!"*
