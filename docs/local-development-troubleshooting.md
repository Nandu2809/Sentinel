# Sentinel Local Development Troubleshooting Log

This document records the empirical root causes and resolved issues discovered during local development setup.

---

### ISSUE 1: Python System Executable Incompatibility with PyTorch 2.2.0

- **PROBLEM**: Running `pip install -r requirements.txt` or `python -m app.main` failed with:
  `ERROR: Could not find a version that satisfies the requirement torch==2.2.0`
- **ROOT CAUSE**: The default host system environment uses Python 3.14 (`C:\Python314\python.exe`). PyTorch 2.2.0 does not distribute pre-built wheels for Python 3.14.
- **FIX**: Created a dedicated Python 3.12 virtual environment at `ai-engine/.venv` (`py -3.12 -m venv .venv`) and configured helper scripts `scripts/run-ai-engine.ps1` and `ai-engine/run.ps1` to execute via `.venv\Scripts\python.exe`.
- **VERIFICATION**:
  ```powershell
  .\scripts\run-ai-engine.ps1
  # Output: Starting Sentinel AI Engine on http://localhost:8000 using Python 3.12 (.venv)...
  ```

---

### ISSUE 2: NumPy 2.x Conflict with PyTorch 2.2.0 C++ Extensions

- **PROBLEM**: Running `import torch` inside Python 3.12 raised:
  `UserWarning: Failed to initialize NumPy: _ARRAY_API not found`
- **ROOT CAUSE**: `requirements.txt` specified `numpy>=1.26.0`, which installed NumPy 2.2+. PyTorch 2.2.0 C++ extensions require NumPy 1.x ABI (`numpy<2.0`).
- **FIX**: Pin `numpy<2.0` (installed `numpy==1.26.4`) and `scipy<1.15.0` (`scipy==1.14.1`) inside `ai-engine/.venv`.
- **VERIFICATION**:
  ```powershell
  & ".\ai-engine\.venv\Scripts\python.exe" -c "import torch, numpy; print(torch.__version__, numpy.__version__)"
  # Output: 2.2.0+cpu 1.26.4
  ```

---

### ISSUE 3: Maven `spring-boot:run` Failure on Parent Aggregator POM

- **PROBLEM**: Running `.\scripts\run-auth-service.ps1` failed with:
  `[ERROR] Failed to execute goal ... on project sentinel-backend: Unable to find a suitable main class`
- **ROOT CAUSE**: `run-auth-service.ps1` executed `mvn -pl auth-service -am spring-boot:run` from the root aggregator POM directory. Maven attempted to run the `spring-boot:run` goal on the parent POM `sentinel-backend`, which has packaging `pom` and no main class.
- **FIX**: Updated `scripts/run-auth-service.ps1` to set location into `backend/auth-service` before executing `mvn spring-boot:run`.
- **VERIFICATION**:
  ```powershell
  ..\scripts\run-auth-service.ps1
  # Output: Started AuthServiceApplication using Java 21.0.12 on port 8081
  ```
