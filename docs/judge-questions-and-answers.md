# Sentinel — Judge Questions & Answers Database

## Technical Questions

### 1. Why Kafka?
Kafka provides asynchronous, high-throughput, fault-tolerant event streaming that decouples real-time payment ingestion from downstream ML evaluation, risk scoring, and alert creation.

### 2. Why microservices?
Microservice architecture allows independent scaling, isolation, and technological specialization. For example, `risk-service` scales horizontally for ML scoring, while `auth-service` manages identity without bottlenecking event evaluation.

### 3. Why Python for ML and Java for backend?
Java 21 Spring Boot delivers enterprise-grade concurrency, type safety, and microservice ecosystem integration for event routing and persistence. Python FastAPI provides access to scientific ML libraries (Scikit-learn, PyTorch, Pandas).

### 4. Why graph features?
Transaction-level features miss multi-account coordination. In-memory graph features (hardware fingerprint sharing, IP subnet concentration, payment token reuse) expose distributed fraud rings operating across multiple accounts.

### 5. Why Random Forest?
Random Forest provides robust non-linear decision boundaries, handles tabular data cleanly without extensive hyperparameter tuning, resists overfitting, and outputs feature importances for Explainable AI.

### 6. Why not deep learning?
Deep learning models on tabular financial datasets often require larger training volumes, lack native feature explainability, consume significantly higher inference compute, and risk overfitting on synthetic benchmarks.

### 7. How is temporal leakage prevented?
Events are ordered chronologically. Historical rolling aggregations (1-hour velocity, 24-hour failed count) only compute windowed statistics over strictly past timestamps (`t < t_event`).

### 8. How is target leakage prevented?
`StandardScaler` parameters were fitted **exclusively on `train.csv`**. Decision thresholds were tuned **exclusively on `validation.csv`**. Ground-truth labels (`isFraud`, `fraudScenario`) were completely removed from feature input matrices.

### 9. Why did your model achieve 1.0 ROC-AUC / PR-AUC?
The synthetic benchmark dataset ($N=10,000$) exhibits clear linear and non-linear separability across generated scenario rules. In production, noisy real-world data would yield realistic ROC-AUC metrics around 0.92–0.96.

### 10. Is the dataset real?
No. The dataset is a synthetically generated benchmark dataset ($N=10,000$) designed with 8 fraud scenarios to benchmark model behavior safely without exposing PII or real merchant payment records.

### 11. How would this work with real Razorpay data?
In production, Sentinel would ingest live transaction webhooks/Kafka streams from Razorpay gateways, stream telemetry to `FinancialRiskEventConsumer`, and maintain entity graphs in Redis/Neo4j.

### 12. How would the system scale?
Kafka partitions allow horizontal scaling of consumer groups. Microservices deploy as stateless Kubernetes pods with Horizontal Pod Autoscalers (HPA).

### 13. What happens if Kafka goes down?
The Spring Gateway and REST endpoints fall back to synchronous direct service invocation via Resilience4j circuit breakers, ensuring zero transaction drop.

### 14. What happens if the ML engine fails?
`RiskCalculationService` uses a deterministic rule-based fallback provider that scores transactions using heuristic feature bounds, maintaining baseline security.

### 15. How are false positives handled?
Sub-threshold suspicious transactions ($0.25 \le p < 0.50$) route to `REVIEW` instead of `BLOCK`. Analysts can inspect attributions in `/financial-risk` and resolve issues without customer block friction.

### 16. How are false negatives handled?
Missed fraud triggers post-event chargeback analysis. Graph features capture newly linked entities, automatically updating risk weights for subsequent transactions in the ring.

### 17. Why not simply block every high-risk transaction?
Blocking transactions incurs false positive costs ($C_{\text{FP}} = \text{₹1,200}$) and customer churn. Setting $\tau_{\text{block}} = 0.50$ balances chargeback prevention against customer friction.

### 18. How is explainability achieved?
Sentinel extracts feature attributions, comparing observed signal values against baseline percentiles, and renders human-readable primary detection reasons in the Explainable AI panel.

---

## Business Questions

### 19. Who pays for this?
Payment gateways (like Razorpay) and enterprise merchants subscribe to Sentinel to reduce chargebacks, lower manual review costs, and preserve checkout conversion rates.

### 20. How does this reduce financial loss?
By blocking high-risk fraud rings ($p \ge 0.50$), Sentinel prevents chargeback losses ($C_{\text{FN}} = \text{₹6,800}$ per incident).

### 21. Why is REVIEW useful?
Routing ambiguous events ($0.25 \le p < 0.50$) to analyst `REVIEW` at ₹400 cost prevents losing ₹1,200 in false positive customer friction on high-value legitimate checkouts.

### 22. How does this reduce unnecessary customer friction?
Graph features disambiguate shared corporate subnets, reducing false positive blocks on legitimate corporate users from 6 FP to 0 FP.

### 23. What is the value of relationship intelligence?
Relationship graph features improved evasive fraud recall from $95.65\% \rightarrow 100.0\%$, catching coordinated fraud rings that pass standard single-transaction checks.

### 24. What would deployment require?
Containerized deployment via Docker Compose or Helm chart onto existing Kubernetes clusters, connecting to Razorpay event streams and PostgreSQL/Kafka infrastructure.

---

## Security & Hackathon Questions

### 25. How are APIs protected?
Spring Cloud Gateway validates JWT bearer tokens, enforces Redis token-bucket rate limiting, and verifies RBAC roles (`ROLE_USER`, `ROLE_ADMIN`).

### 26. How is JWT handled?
Stateless HS256/RS256 JWT tokens with 15-minute expiration. Revocation is managed via Redis JWT blacklist.

### 27. How are analyst actions audited?
All decision overrides, threshold updates, and incident status changes write immutable activity logs to PostgreSQL audit tables.

### 28. How are sensitive credentials protected?
Environment variables injected via Docker secrets or Kubernetes secret objects. No production secrets are committed in git.

### 29. How would you prevent abuse of BLOCK_IP / DISABLE_USER actions?
Action execution requires `ROLE_ADMIN` authentication, multi-factor confirmation, and triggers mandatory audit log logging.

### 30. What is the most innovative part?
The integration of in-memory relationship graph topology with asymmetric business cost optimization and unified SOC incident management.

### 31. What is the biggest limitation?
Current evaluation uses synthetic datasets; production deployment requires re-training on 90-day historical payment logs.

### 32. What would you build next?
Sub-millisecond graph neural network (GNN) embeddings using PyTorch Geometric and real-time streaming graph database integration (Memgraph/Neo4j).

### 33. Why should we select Sentinel?
Sentinel is a complete, working, multi-service platform that bridges AI research with operational financial security, delivering clear business value and judge-ready runtime evidence.
