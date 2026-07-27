# CI/CD Pipeline Documentation

## Workflows Included
- `.github/workflows/backend-ci.yml`: Java 21 Maven compilation, unit testing, and JaCoCo coverage publishing.
- `.github/workflows/ai-ci.yml`: Python 3.12 dependency verification, pytest suite execution.
- `.github/workflows/security-scan.yml`: Trivy container vulnerability scanning and Gitleaks secret detection.
- `.github/workflows/deploy.yml`: Dry-run validation and deployment to target Kubernetes cluster.
