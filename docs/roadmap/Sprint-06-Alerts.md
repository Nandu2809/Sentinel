# Sprint 06: Alert Management & Multi-Channel Notifications

## Objectives
Build `alert-service` and `notification-service` to manage the security alert lifecycle, escalation policies, deduplication, and multi-channel notification dispatches (Slack, PagerDuty, Email, Webhooks).

## Features
- **Alert Lifecycle Management**: Manage alert states (`NEW`, `ACKNOWLEDGED`, `IN_INVESTIGATION`, `RESOLVED`, `FALSE_POSITIVE`).
- **Alert Deduplication**: Group correlated security incidents occurring within configurable time windows.
- **Escalation Policies**: Trigger multi-tiered notifications based on severity levels (Low, Medium, High, Critical).
- **Multi-Channel Integrations**: Dispatch alerts via Webhooks, Slack channels, Email (SMTP), and PagerDuty API.

## Acceptance Criteria
1. Critical threats automatically send PagerDuty and Slack alerts within 5 seconds of detection.
2. Identical alert triggers within 10 minutes are grouped into a single parent incident.
3. Operators can acknowledge and resolve alerts via API endpoints.

## Dependencies
- Sprint 04 (`threat-service`), `common-library`, external webhook APIs.

## Deliverables
- `alert-service` and `notification-service` microservice modules.
- Webhook dispatch handlers and template engines.
- Alert management REST APIs.
