# Sentinel Phase 8 — Demo Day Copy-Paste Command Sheet

This command sheet contains ONLY the verified, copy-paste terminal commands required for demo day presentation, testing, and recovery.

---

## 🚀 1. START STACK

```bash
# Navigate to project root
cd Sentinel

# Compile backend microservices
mvn clean package -DskipTests

# Start Docker containers
docker compose -f infrastructure/docker/docker-compose.yml up -d --build

# Start Angular Frontend Dev Server
cd frontend/sentinel
npm run start
```

---

## 🔍 2. HEALTH & STATUS VERIFICATION

```bash
# Check Docker container status
docker compose -f infrastructure/docker/docker-compose.yml ps

# Check microservice health endpoints
curl http://localhost:8088/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8000/health
```

---

## 🔑 3. AUTHENTICATION & LOGIN

```bash
# Perform JWT Login
curl -X POST http://localhost:8088/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@sentinel.com","password":"Admin@123456!"}'
```

---

## 🧪 4. SCENARIO EVALUATIONS (LIVE DEMO)

### Scenario A — Legitimate (APPROVE)
```bash
curl -X POST http://localhost:8088/api/v1/financial-risk/evaluate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "transactionId": "tx-demo-scenA-001",
    "userId": "usr-legit-001",
    "merchantId": "mch-store-001",
    "amount": 2450.0,
    "currency": "INR",
    "timestamp": 1770000000000,
    "deviceId": "dev-legit-001",
    "ipAddress": "103.21.244.15",
    "location": "Mumbai, IN",
    "paymentMethodRef": "pay-upi-9988",
    "accountAgeDays": 180,
    "velocity1h": 1,
    "failedTxCount24h": 0,
    "sharedDeviceAccountCount": 1,
    "sharedIpAccountCount": 1
  }'
```

### Scenario B — Ambiguous (REVIEW)
```bash
curl -X POST http://localhost:8088/api/v1/financial-risk/evaluate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "transactionId": "tx-demo-scenB-001",
    "userId": "usr-ambig-002",
    "merchantId": "mch-store-001",
    "amount": 14500.0,
    "currency": "INR",
    "timestamp": 1770000000000,
    "deviceId": "dev-ambig-002",
    "ipAddress": "103.21.244.50",
    "location": "Delhi, IN",
    "paymentMethodRef": "pay-card-4455",
    "accountAgeDays": 30,
    "velocity1h": 4,
    "failedTxCount24h": 1,
    "sharedDeviceAccountCount": 2,
    "sharedIpAccountCount": 6
  }'
```

### Scenario C — Fraud Ring (BLOCK)
```bash
curl -X POST http://localhost:8088/api/v1/financial-risk/evaluate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "transactionId": "tx-demo-scenC-001",
    "userId": "usr-ring-leader-999",
    "merchantId": "mch-highrisk-999",
    "amount": 48500.0,
    "currency": "INR",
    "timestamp": 1770000000000,
    "deviceId": "dev-ring-master-999",
    "ipAddress": "198.51.100.99",
    "location": "Unknown, IN",
    "paymentMethodRef": "pay-card-ring-99",
    "accountAgeDays": 2,
    "velocity1h": 9,
    "failedTxCount24h": 4,
    "sharedDeviceAccountCount": 8,
    "sharedIpAccountCount": 11
  }'
```

---

## 🧪 5. AUTOMATED TEST SUITES

```bash
# Run Maven Backend Unit & Integration Tests
mvn clean test

# Run Angular Production Build
cd frontend/sentinel && npm run build

# Run Python AI Engine Unit Tests
python -m unittest tests/test_phase6f_financial.py
```

---

## 🚨 6. RESET & RECOVERY

```bash
# Soft Reset Transaction Data
curl -X POST http://localhost:8088/api/v1/financial-risk/reset \
  -H "Authorization: Bearer <JWT_TOKEN>"

# Open Mailpit Webmail Interface
http://localhost:8025
```
