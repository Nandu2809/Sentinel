# Sprint 08: Production Hardening, Kubernetes Orchestration & Infrastructure-as-Code

## Objectives
Prepare the entire Sentinel platform for production deployment with Terraform infrastructure provisioning, Kubernetes manifests, Helm packaging, production security hardening, and complete CI/CD automation.

## Features
- **Kubernetes Deployment Manifests**: K8s deployments, services, ingress rules, ConfigMaps, and SealedSecrets (`infrastructure/kubernetes/`).
- **Terraform IaC**: AWS/GCP infrastructure module definitions (`infrastructure/terraform/`).
- **Production Hardening**: Non-root container execution, read-only root filesystems, network policies, and TLS termination.
- **Full CI/CD Pipeline**: GitHub Actions workflows for continuous integration, image scanning (Trivy), container registry publishing, and automated deployments.

## Acceptance Criteria
1. `kubectl apply -f infrastructure/kubernetes/` deploys the full platform with zero errors.
2. All Docker containers pass Trivy vulnerability scans with 0 High/Critical CVEs.
3. Automated GitHub Actions CI/CD builds, tests, packages, and verifies the entire monorepo on every release tag.

## Dependencies
- All Sprints (01-07), Kubernetes, Helm, Terraform, Docker, GitHub Actions.

## Deliverables
- Helm charts and Kubernetes manifests.
- Terraform IaC scripts.
- Completed GitHub Actions workflows (`.github/workflows/`).
