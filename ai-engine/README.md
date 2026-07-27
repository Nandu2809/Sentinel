# Sentinel AI Engine

The **Sentinel AI Engine** provides real-time anomaly detection, threat pattern scoring, and predictive API telemetry analysis for the Sentinel platform.

## Directory Structure

```text
ai-engine/
├── anomaly-detection/   # Core inference models and API detectors
├── training/            # Offline model training and validation scripts
├── models/              # Exported model weights and artifacts
├── datasets/            # Training/testing feature sets and telemetry logs
├── pipelines/           # Data preprocessing and feature engineering pipelines
├── experiments/         # ML experiments and hyperparameter optimization
├── requirements.txt     # Python dependency specifications
└── README.md            # Module documentation
```

## Setup & Installation

1. Create a virtual environment:
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: .\venv\Scripts\Activate.ps1
   ```
2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
