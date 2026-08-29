#!/usr/bin/env python3
"""
Sentinel Phase 6E — AI Engine Integration for Risk Cost Benchmark Tests
"""

import sys
import os
import unittest

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "../../../")))
from tests.test_financial_risk_cost import TestFinancialRiskCost

if __name__ == "__main__":
    unittest.main()
