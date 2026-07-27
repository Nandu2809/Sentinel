# Kubernetes Operations Guide

## Namespace Initialization
```bash
kubectl apply -f infrastructure/kubernetes/namespace.yaml
```

## Secrets & ConfigMaps
```bash
kubectl apply -f infrastructure/kubernetes/configmaps/sentinel-configmap.yaml
kubectl apply -f infrastructure/kubernetes/secrets/sentinel-secrets.yaml.example
```

## Persistent Storage & Deployments
```bash
kubectl apply -f infrastructure/kubernetes/persistent-volumes/
kubectl apply -f infrastructure/kubernetes/deployments/
kubectl apply -f infrastructure/kubernetes/services/
kubectl apply -f infrastructure/kubernetes/ingress/
kubectl apply -f infrastructure/kubernetes/autoscaling/
```

## Horizontal Pod Autoscaling (HPA)
Deployments automatically scale out up to 10 pod replicas when CPU usage exceeds 70% or memory exceeds 80%.
