#!/usr/bin/env python3
"""
Sentinel Phase 6C — AI Engine Test Integration for Financial Feature Graph
"""

import sys
import os
import unittest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../../../")))
from tests.test_financial_feature_graph import TestFinancialFeatureGraph

if __name__ == "__main__":
    unittest.main()
