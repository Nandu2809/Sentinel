# Contributing to Sentinel Platform

Thank you for your interest in contributing to **Sentinel – Intelligent API Security & Threat Monitoring Platform**!

## Development Workflow

1. **Fork & Clone** the repository:
   ```bash
   git clone https://github.com/madugundunanda-ui/Sentinel.git
   ```

2. **Branching Model**:
   - Create feature branches off `develop`: `git checkout -b feature/my-feature`
   - For bug fixes: `git checkout -b fix/my-bugfix`

3. **Code Formatting & Verification**:
   - For Java backend: ensure `$env:JAVA_HOME="C:\Program Files\Java\jdk-22"; mvn clean test` passes with zero failures.
   - For Python AI Engine: ensure `pytest -v tests/` passes.

4. **Pull Request Guidelines**:
   - Include unit tests covering new logic (>80% coverage required).
   - Ensure no secrets or credentials are included.
