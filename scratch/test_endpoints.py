import urllib.request
import json
import time
import base64
import hmac
import hashlib

# 1. Generate fresh JWT
b64 = lambda d: base64.urlsafe_b64encode(json.dumps(d, separators=(',', ':')).encode()).decode().rstrip('=')
header = {'alg': 'HS256', 'typ': 'JWT'}
payload = {
    'sub': 'admin',
    'userId': '00000000-0000-0000-0000-000000000001',
    'iss': 'sentinel-auth-service',
    'roles': ['SECURITY_ADMIN'],
    'permissions': ['INCIDENT_READ', 'INCIDENT_WRITE'],
    'exp': int(time.time()) + 86400
}
msg = f"{b64(header)}.{b64(payload)}"
sig = base64.urlsafe_b64encode(hmac.new(b'change-me-very-long-secret-key-2026!', msg.encode(), hashlib.sha256).digest()).decode().rstrip('=')
token = f"{msg}.{sig}"

endpoints = [
    ("1. Gateway Actuator Health", "http://localhost:8088/actuator/health", "GET", None, None),
    ("2. Alert Service Actuator Health", "http://localhost:8085/actuator/health", "GET", None, None),
    ("3. Gateway Incidents List", "http://localhost:8088/api/v1/incidents", "GET", token, None),
    ("4. Gateway Threat Hunting Search", "http://localhost:8088/api/v1/threat-hunting/search", "GET", token, None),
    ("5. Gateway Trigger Impossible Travel Test", "http://localhost:8088/api/v1/incidents/test/trigger-impossible-travel", "POST", token, b"")
]

results = []
for name, url, method, auth_token, body in endpoints:
    print(f"Testing {name}...", flush=True)
    req = urllib.request.Request(url, method=method, data=body)
    if auth_token:
        req.add_header("Authorization", f"Bearer {auth_token}")
    if body is not None:
        req.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            status = resp.status
            content = resp.read().decode('utf-8')
            print(f"  -> SUCCESS {status}: {content[:150]}", flush=True)
            results.append({"test": name, "url": url, "status": status, "body": content[:300]})
    except urllib.error.HTTPError as e:
        body_err = e.read().decode('utf-8')
        print(f"  -> HTTP ERROR {e.code}: {body_err[:150]}", flush=True)
        results.append({"test": name, "url": url, "status": e.code, "body": body_err[:300]})
    except Exception as e:
        print(f"  -> EXCEPTION: {e}", flush=True)
        results.append({"test": name, "url": url, "status": "ERROR", "error": str(e)})

print("\n--- FINAL SUMMARY ---")
print(json.dumps(results, indent=2))
