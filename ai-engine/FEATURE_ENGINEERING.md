# Feature Engineering Specification

## Extracted Features

| Feature Name | Type | Formula / Transformation | Security Purpose |
| :--- | :--- | :--- | :--- |
| `is_get` | Binary | `1.0 if method == 'GET' else 0.0` | Identifies verb baseline |
| `is_post` | Binary | `1.0 if method == 'POST' else 0.0` | State-changing operation flag |
| `is_put` | Binary | `1.0 if method == 'PUT' else 0.0` | Resource modification flag |
| `is_delete` | Binary | `1.0 if method == 'DELETE' else 0.0` | High-risk deletion action |
| `endpoint_depth` | Float | `count('/')` | Detects deep URI traversal |
| `uri_entropy` | Float | $-\sum p_i \log_2(p_i)$ | Detects obfuscated payloads/SQLi/XSS |
| `log_payload_size` | Float | $\log(1 + \text{payload\_bytes})$ | Normalizes skewed upload sizes |
| `log_response_size` | Float | $\log(1 + \text{response\_bytes})$ | Detects data exfiltration spikes |
| `log_response_time` | Float | $\log(1 + \text{latency\_ms})$ | Detects DoS / ReDoS / Slowloris |
| `is_error` | Binary | `1.0 if status >= 400 else 0.0` | Rejection and fuzzing signal |
| `login_freq_1h` | Float | Raw count within 60m | Credential stuffing & brute-force flag |
