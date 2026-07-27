# Docker Deployment Guide

## Local Development Spin-Up
```bash
# 1. Clone & prepare environment
cp .env.example .env

# 2. Build and start all services
docker compose -f infrastructure/docker/docker-compose.yml up --build -d

# 3. Check health of services
docker compose -f infrastructure/docker/docker-compose.yml ps
```

## Security Best Practices Enforced
- All Java and Python runtime containers run under unprivileged non-root UID `10001`.
- Multi-stage builds strip Maven & Pip toolchains from final production runtime images.
- Actuator health probes monitor readiness and liveness.
