# Sprint 07: Machine Learning Anomaly Detection Engine

## Objectives
Deploy the Python `ai-engine` featuring Isolation Forest and Autoencoder models for unsupervised API anomaly detection, bot signature analysis, and predictive threat identification.

## Features
- **Feature Extraction Pipeline**: Preprocess HTTP method distributions, payload size metrics, header entropy, and request inter-arrival times.
- **Unsupervised Anomaly Model**: Train Isolation Forest and PyTorch Autoencoders to detect out-of-distribution API request patterns.
- **Inference Microservice**: Expose a FastAPI REST server (`ai-engine/anomaly-detection/`) for low-latency anomaly scoring.
- **Model Lifecycle & Versioning**: Automated model retraining pipelines and versioned model artifact storage (`ai-engine/models/`).

## Acceptance Criteria
1. Feature extraction processes 10,000 telemetry events per second.
2. Inference API responds with anomaly probability scores in <15ms.
3. Model successfully flags zero-day API scraping attacks missed by static rules.

## Dependencies
- Sprint 03 (`monitoring-service`), Python 3.11+, PyTorch, Scikit-Learn, FastAPI.

## Deliverables
- Fully functional `ai-engine/` module (`requirements.txt`, models, pipelines).
- Integration bridge between `threat-service` and `ai-engine` FastAPI service.
