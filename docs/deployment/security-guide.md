# DevSecOps Security Control Policies

## Key Policies
1. **Container Hardening**: Base images built on minimal Alpine Linux. Containers execute as non-root UID `10001`.
2. **Secrets Hygiene**: Credentials passed solely via Kubernetes Secrets and environment variables.
3. **Network Policies**: Database & Redis ports isolated from ingress and restricted exclusively to internal backend pod communication.
4. **Vulnerability Gate**: Automated Trivy scans fail CI builds on `CRITICAL` vulnerability findings.
