# Sentinel Phase 8 — 30-Question Judge Q&A Master Sheet

This master sheet contains concise, technically defensible, and scientifically honest answers for the 30 core judge questions for Razorpay AI Buildathon 2026 (Track 2: AI Risk Manager).

---

### 1. What problem does Sentinel solve?
Sentinel addresses single-transaction fraud blind spots by evaluating multi-hop relationships across users, devices, IPs, and payment instruments, preventing evasive fraud rings while protecting legitimate users.

### 2. Why isn't this just another fraud model?
Standard fraud models treat transactions as isolated data rows. Sentinel fuses in-memory entity graph topology, behavioral machine learning, asymmetric business cost optimization, and SOC incident escalation into an operational workflow.

### 3. Why use a graph?
Evasive fraud rings stay under individual velocity thresholds by rotating accounts. Graph topology reveals hidden structural dependencies (e.g. 8 accounts sharing 1 device) regardless of individual velocity.

### 4. Why use AI?
AI/ML handles complex, non-linear relationships across 34 feature dimensions that static rule engines cannot capture without creating thousands of brittle, conflicting rules.

### 5. What does AI actually contribute?
The AI model outputs a calibrated fraud probability $p \in [0.0, 1.0]$ based on feature interactions, allowing business cost optimization to select dynamic operating cutoffs.

### 6. What happens when the model is wrong?
Sentinel uses a 3-tier policy (`APPROVE` / `REVIEW` / `BLOCK`). Borderline predictions ($0.25 \le p < 0.50$) route to Human-in-the-Loop SOC analysts rather than causing outright customer drop-off.

### 7. Why REVIEW instead of BLOCK?
An automated `BLOCK` on an ambiguous transaction incurs a high false-positive penalty ($C_{\text{FP}} = \text{₹1,200}$). Routing to `REVIEW` costs only $C_{\text{REVIEW}} = \text{₹400}$ analyst labor while preventing customer loss.

### 8. How does cost-sensitive decisioning work?
Operating cutoffs $\tau_{\text{block}}$ and $\tau_{\text{review}}$ are calculated by minimizing total expected business loss ($\mathbb{E}[\text{Cost}] = C_{\text{FP}}N_{\text{FP}} + C_{\text{FN}}N_{\text{FN}} + C_{\text{REVIEW}}N_{\text{REVIEW}} + C_{\text{FRICTION}}N_{\text{FRICTION}}$).

### 9. How do shared devices affect legitimate users?
Shared devices (e.g., family tablets) increment device account counts. Sentinel combines device sharing with behavioral velocity and payment instrument reuse so legitimate shared devices are not falsely blocked.

### 10. How do shared IPs affect legitimate users?
Shared IPs (e.g., corporate NATs, university Wi-Fi) have high IP account counts. Graph features decouple IP fan-out from device sharing, preserving high precision on corporate subnets.

### 11. How is data leakage prevented?
Scaler transformations are fit strictly on training splits (`train.csv`), thresholds are tuned on validation splits (`validation.csv`), and held-out test data is evaluated strictly once.

### 12. How was the held-out test set created?
The dataset was partitioned into 70% Train (700), 10% Validation (100), and 20% Held-Out Test (200) splits with fixed random seed stratification.

### 13. Is the dataset real?
No. The dataset is a synthetic demonstration benchmark engineered with controlled fraud ring topologies and legitimate shared infrastructure patterns.

### 14. Why are the metrics high?
Near-perfect AUC scores occur because synthetic scenario patterns possess clean boundary separability. We explicitly disclose that real payment telemetry will exhibit higher noise and lower accuracy.

### 15. Can this use real payment data?
Yes. Sentinel consumes standard payment telemetry fields (`amount`, `currency`, `deviceId`, `ipAddress`, `paymentMethodRef`) via REST or Kafka envelopes without requiring proprietary schema changes.

### 16. How would Sentinel integrate with a payment gateway like Razorpay?
Sentinel functions as an asynchronous/synchronous risk sidecar via REST or Kafka. Razorpay Gateway passes transaction envelopes to `/api/v1/financial-risk/evaluate` before authorization.

### 17. Why Kafka?
Kafka enables non-blocking event streaming, allowing risk evaluations to broadcast alert events (`sentinel.alert-events`) asynchronously without adding latency to the primary payment gateway thread.

### 18. Why microservices?
Microservices decouple compute-heavy AI/Graph evaluation (`risk-service`, `ai-engine`) from auth, gateway, alerting, and reporting, ensuring independent scalability and fault tolerance.

### 19. How does the relationship graph scale?
For hackathon demonstration scale, Sentinel utilizes an in-memory graph. For petabyte production scale, the topology engine delegates to distributed graph databases such as Neo4j or AWS Neptune.

### 20. How does explainability work?
Sentinel outputs explicit risk factors (e.g. `HIGH_DEVICE_SHARING`, `IP_SUBNET_FANOUT`, `ELEVATED_VELOCITY`) alongside graph topology statistics in every evaluation response.

### 21. What happens after BLOCK?
An automated `BLOCK` publishes an alert event to Kafka, auto-generates a SOC Incident (`INC-2026-XXXX`), dispatches an email notification via Mailpit, and updates the SOC Command Center.

### 22. How does the analyst investigate?
Analysts log into the SOC workstation (`/incidents`), inspect incident timelines, view graph node connectivity, record investigation notes, and trigger threat hunting queries (`/threat-hunting`).

### 23. What are the current limitations?
Current limitations include synthetic dataset evaluation, in-memory graph scope, simplified graph topology, and absence of live Razorpay production transaction feedback loops.

### 24. What would be required for production?
Production readiness requires distributed graph database backing (Neo4j/Neptune), real-time online model retraining pipelines, multi-region Kafka replication, and A/B shadow mode evaluation.

### 25. What is Sentinel's strongest differentiator?
The combination of multi-hop relationship graph intelligence with asymmetric business cost optimization and direct SOC incident escalation.

### 26. Why is this relevant to Track 2?
Track 2 ("AI Risk Manager") calls for intelligent risk management. Sentinel moves beyond passive risk scoring into operational risk governance and cost-optimized decisioning.

### 27. How does Sentinel handle false positives?
By establishing the `REVIEW` tier, ambiguous transactions undergo human validation rather than automated rejection, minimizing customer loss.

### 28. How is business loss represented?
Business loss is modeled explicitly as a weighted cost sum reflecting chargeback penalties ($C_{\text{FN}} = \text{₹6,800}$), churn loss ($C_{\text{FP}} = \text{₹1,200}$), and review overhead ($C_{\text{REVIEW}} = \text{₹400}$).

### 29. How would model drift be handled?
In production, Sentinel monitors drift using Population Stability Index (PSI) on graph feature distributions, triggering automated pipeline retrains when drift exceeds 0.15 PSI.

### 30. How would real-world feedback improve the model?
SOC analyst review decisions (`APPROVED_BY_ANALYST` / `CONFIRMED_FRAUD`) stream back into historical feature store datasets as ground-truth labels for continuous model fine-tuning.
