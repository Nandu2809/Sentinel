# Sentinel Phase 7 — Demo Failure Recovery & Emergency Operating Guide

During a live hackathon judge demonstration, technical failures must be resolved calmly using predefined diagnostic steps. **NEVER modify source code or hardcode fake UI results during a presentation.**

---

## Emergency Action Matrix

| Failure Symptom | Cause | Diagnostic Command | Recovery Action |
| :--- | :--- | :--- | :--- |
| **Frontend UI Not Loading / Unresponsive** | Dev server stopped or crashed | `Get-NetTCPConnection -LocalPort 4200` | Restart Angular dev server:<br>`npx ng serve --host 0.0.0.0 --port 4200` |
| **API Gateway 502 / 504 Errors** | Gateway container unresponsive | `docker logs sentinel-gateway-service --tail 50` | Restart API Gateway:<br>`docker restart sentinel-gateway-service` |
| **AI Engine / ML Model Errors** | Python runtime exception | `docker logs sentinel-ai-engine --tail 50` | Restart AI Engine container:<br>`docker restart sentinel-ai-engine` |
| **Kafka Event Stream Stalled** | Broker or Zookeeper disconnection | `docker compose -f infrastructure/docker/docker-compose.yml ps kafka zookeeper` | Restart Kafka stack:<br>`docker restart sentinel-zookeeper sentinel-kafka` |
| **Database Connection Refused** | PostgreSQL connection pool full or container stopped | `docker exec -it sentinel-postgres pg_isready` | Restart PostgreSQL container:<br>`docker restart sentinel-postgres` |
| **Unexpected Scenario Result** | Edge-case telemetry variation | Inspect `/api/v1/financial-risk/decisions` | **DO NOT alter code during demo.** Explain feature influence honestly to judges. |

---

## 30-Second Rapid Recovery Script

If the entire stack needs a rapid health refresh:

```powershell
# 1. Check container health
docker compose -f infrastructure/docker/docker-compose.yml ps

# 2. Restart unhealthy microservices only
docker restart sentinel-auth-service sentinel-risk-service sentinel-gateway-service

# 3. Verify health matrix
python scratch/test_health.py
```
