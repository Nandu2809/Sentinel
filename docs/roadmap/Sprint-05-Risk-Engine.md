# Sprint 05: Behavioral Risk Engine & User Profiling

## Objectives
Build contextual risk profiling models within `threat-service` that compute baseline user behavior (customary login hours, typical user agents, geographic origins) to detect account takeover and anomalous usage patterns.

## Features
- **User Baseline Profiler**: Aggregate historical access logs to form behavioral fingerprints for active users.
- **Contextual Risk Assessor**: Evaluate login requests against baseline geographic location, device fingerprint, and time-of-day.
- **Adaptive Authentication Trigger**: Signal gateway to require Step-Up MFA when risk scores cross policy thresholds.
- **Risk Score History**: Persist historical risk trajectory data for forensic analysis.

## Acceptance Criteria
1. Logins from a new country flag a medium-risk score (50-75) and trigger Step-Up MFA requirement.
2. Concurrent logins from geographically impossible locations (e.g. US and Asia within 5 minutes) generate a critical threat event.
3. Risk scores decay predictably over time following clean user activity.

## Dependencies
- Sprint 01 (`auth-service`), Sprint 04 (`threat-service`), GeoIP2 database.

## Deliverables
- Risk evaluation algorithms and baseline profilers.
- Adaptive authentication contract definitions in `common-library`.
- Risk history database schemas and migration scripts.
