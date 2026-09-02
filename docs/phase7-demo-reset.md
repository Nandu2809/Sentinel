# Sentinel Phase 7 — Safe Deterministic Demo Reset Procedure

This document specifies the safe, deterministic procedure to reset Sentinel's runtime data state before or between live hackathon judge demonstrations.

## Reset Guarantees
- ❌ **NO source code deletion.**
- ❌ **NO destructive volume deletion (`docker volume rm`).**
- ❌ **NO modification of trained ML models or feature extractors.**
- ✅ **Safe reset of test database rows and cached UI telemetry.**

---

## Standard Reset Procedure

### Option 1: Soft Data Reset (Recommended)
Clears temporary demo transaction records while maintaining schema migrations and bootstrap administrative accounts.

```powershell
# Execute PostgreSQL table clean via Docker
docker exec -i sentinel-postgres psql -U sentinel_admin -d sentinel_db -c "
  TRUNCATE TABLE risk.risk_assessments RESTART IDENTITY CASCADE;
  TRUNCATE TABLE alert.alerts RESTART IDENTITY CASCADE;
  TRUNCATE TABLE threat.incidents RESTART IDENTITY CASCADE;
  TRUNCATE TABLE threat.timeline_events RESTART IDENTITY CASCADE;
"
```

### Option 2: Clean Stack Restart
Restarts microservices and re-seeds baseline bootstrap admin accounts without volume loss.

```powershell
# Safe restart of Docker microservices
docker compose -f infrastructure/docker/docker-compose.yml restart auth-service risk-service alert-service threat-service
```

### Option 3: Mailpit Notification Clear
Clears temporary Mailpit email inbox.

```powershell
# Delete all emails in Mailpit
Invoke-RestMethod -Uri "http://localhost:8025/api/v1/messages" -Method DELETE
```
