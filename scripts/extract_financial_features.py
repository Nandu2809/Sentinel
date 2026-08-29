#!/usr/bin/env python3
"""
Sentinel Phase 6C — Financial Feature Extraction CLI Script
"""

import sys
import os

# Add repository root to sys.path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../")))

from importlib.util import module_from_spec, spec_from_file_location

# Load feature pipeline module directly to handle hyphenated path directory
pipeline_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "../ai-engine/anomaly-detection/app/financial/feature_pipeline.py"))
spec = spec_from_file_location("feature_pipeline", pipeline_path)
feature_pipeline = module_from_spec(spec)
sys.modules["feature_pipeline"] = feature_pipeline
spec.loader.exec_module(feature_pipeline)

if __name__ == "__main__":
    feature_pipeline.run_feature_pipeline()
