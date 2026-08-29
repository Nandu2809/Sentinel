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
    ("1. Gateway Actuator Health", "http://127.0.0.1:8088/actuator/health", "GET", None, None),
    ("2. Alert Service Actuator Health", "http://127.0.0.1:8085/actuator/health", "GET", None, None),
    ("3. Gateway Incidents List (Before)", "http://127.0.0.1:8088/api/v1/incidents", "GET", token, None),
    ("4. Gateway Threat Hunting Search", "http://127.0.0.1:8088/api/v1/threat-hunting/search", "GET", token, None),
    ("5. Trigger Impossible Travel Test Scenario", "http://127.0.0.1:8088/api/v1/incidents/test/trigger-impossible-travel", "POST", token, b""),
    ("6. Gateway Incidents List (After)", "http://127.0.0.1:8088/api/v1/incidents", "GET", token, None),
    ("7. Mailpit Messages Check", "http://127.0.0.1:8025/api/v1/messages", "GET", None, None)
]

results = []
created_incident_id = None

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
            results.append({"test": name, "url": url, "status": status, "body": content})
            
            # If this was trigger impossible travel, capture incident ID or check response
            if "Trigger Impossible Travel" in name:
                try:
                    data = json.loads(content)
                    if isinstance(data, dict) and "id" in data:
                        created_incident_id = data["id"]
                except Exception:
                    pass
    except urllib.error.HTTPError as e:
        body_err = e.read().decode('utf-8')
        print(f"  -> HTTP ERROR {e.code}: {body_err[:150]}", flush=True)
        results.append({"test": name, "url": url, "status": e.code, "body": body_err})
    except Exception as e:
        print(f"  -> EXCEPTION: {e}", flush=True)
        results.append({"test": name, "url": url, "status": "ERROR", "error": str(e)})

if created_incident_id:
    name = "8. Incident Details Check"
    url = f"http://127.0.0.1:8088/api/v1/incidents/{created_incident_id}"
    print(f"Testing {name}...", flush=True)
    req = urllib.request.Request(url, method="GET")
    req.add_header("Authorization", f"Bearer {token}")
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            content = resp.read().decode('utf-8')
            print(f"  -> SUCCESS {resp.status}: {content[:150]}", flush=True)
            results.append({"test": name, "url": url, "status": resp.status, "body": content})
    except Exception as e:
        print(f"  -> EXCEPTION: {e}", flush=True)
        results.append({"test": name, "url": url, "status": "ERROR", "error": str(e)})

print("\n--- FINAL SUMMARY ---")
summary_list = []
for item in results:
    body_summary = item.get("body", "")[:200] if "body" in item else item.get("error", "")
    summary_list.append({"test": item["test"], "status": item["status"], "summary": body_summary})
print(json.dumps(summary_list, indent=2))

