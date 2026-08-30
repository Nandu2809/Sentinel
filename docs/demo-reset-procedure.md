# Sentinel — Demo Reset Procedure

## Overview
This document details how to safely reset the Sentinel demonstration environment to a clean state without destroying source code or configuration.

---

## 🧹 Non-Destructive Demo Reset Commands

### 1. Re-initialize Demo Containers
```bash
cd infrastructure/docker

# Stop containers without removing volumes (preserves schema setups)
docker compose stop

# Restart containers cleanly
docker compose up -d
```

### 2. Reset In-Memory Stream State & Cache
```bash
# Flush Redis JWT / Rate Limit cache if needed
docker exec -it sentinel-redis redis-cli -a change-me-redis-pass! FLUSHALL
```

### 3. Re-run Python ML Pipeline Validation
```bash
# Re-verify synthetic dataset evaluation
python -m unittest tests/test_phase6f_financial.py
```

### 4. Verify Frontend Refresh
- In browser (`http://localhost:4200/financial-risk`), click the **`↻ REFRESH`** button on top header.
- Alternatively, trigger one of the 3 Scenario Quick-Testers (**Scenario A**, **Scenario B**, **Scenario C**) to populate fresh transaction cards.
