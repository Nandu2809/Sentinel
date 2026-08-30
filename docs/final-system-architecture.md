# Sentinel — Final System Architecture

## Architecture Diagram

```
                             Angular SOC Command Center
                                  (/financial-risk)
                                          │
                                          ▼
                                 API Gateway Service
                                    (Port 8080/8088)
                                          │
                  ┌───────────────────────┼───────────────────────┐
                  ▼                       ▼                       ▼
             Auth Service          Financial Risk          Incident Service
             (Port 8081)            (Port 8084)               (Port 8085)
                                          │
                                          ▼
                             Kafka Event Streaming Bus
                             (Topic: sentinel.financial.events)
                                          │
                      ┌───────────────────┼───────────────────┐
                      ▼                   ▼                   ▼
                Threat Service        AI Engine       Monitoring Service
                 (Port 8083)         (Port 8000)          (Port 8082)
                      │                   │
                      └─────────┬─────────┘
                                ▼
                           Risk Engine
                   (Feature Extraction & Graph)
                                │
                                ▼
                     Cost-Aware Decision Engine
                   (APPROVE / REVIEW / BLOCK)
                                │
                      ┌─────────┴─────────┐
                      ▼                   ▼
             Kafka Alert Topic        PostgreSQL
         (sentinel.alert-events)       (Risk DB)
                      │
                      ▼
                Alert Service
                      │
                      ▼
               Incident Service
             (INC-2026-1000 SOC)
                      │
                      ▼
             SOC Investigation & Action
```

---

## Subsystem Functional Summary

1. **Ingestion & Gateway Layer**: Spring Cloud Gateway enforces JWT authentication, CORS header handling, and Redis rate limiting.
2. **Event Streaming Bus**: Apache Kafka manages decoupling and real-time streaming over `sentinel.financial.events`, `sentinel.risk-events`, and `sentinel.alert-events`.
3. **Graph Feature & Risk Calculation**: `FinancialRiskEventConsumer` processes transactions, computes 34 features (including in-memory multi-hop entity sharing), and computes risk probability scores.
4. **Cost-Aware Decision Engine**: Applies decision thresholds ($\tau_{\text{block}} = 0.50$, $\tau_{\text{review}} = 0.25$) to minimize total expected business loss.
5. **SOC Alert & Incident Bridge**: Emits high-risk events to `alert-service`, creates tracking incidents in `incident-service`, and visualizes attributions in Angular.
