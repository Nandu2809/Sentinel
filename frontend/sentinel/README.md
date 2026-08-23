# Sentinel — AI Security Intelligence Platform (Frontend)

Angular 21 frontend for Sentinel, visualizing the security pipeline:
`Auth → Kafka Event Streaming → Monitoring → Threat Detection → Risk Intelligence → Alert Management`.

## Run

```bash
npm install
npm start
```

App serves at `http://localhost:4200`. Log in with any email/password — auth is currently backed
by `AuthService` mocks so the UI is fully explorable before the real API gateway is wired up.

## Architecture

```
src/app/
  core/
    guards/        authGuard, roleGuard (functional CanActivateFn)
    interceptors/   authInterceptor — attaches JWT, retries once on 401 via refresh token
    models/         SecurityEvent, Threat, SecurityAlert, UserRiskProfile, AuthUser…
    services/       AuthService, SecurityEventService, ThreatService, RiskService, AlertService
  layout/           ShellComponent — icon-rail nav + status bar chrome for authenticated routes
  shared/components/
    security-metric-card   HUD bracket-framed KPI tile
    risk-gauge              Radial "Risk Constellation" gauge (signature visualization)
    event-stream             Live monitoring console feed
    threat-timeline           Attack timeline
    alert-panel                Incident card with acknowledge/investigate/resolve actions
    risk-chart, security-map, activity-timeline, severity-badge
  features/
    auth/login, auth/register    Public routes — security gateway experience
    dashboard, alerts, threats, risk, profile, admin   Lazy-loaded, guarded routes
```

## Backend integration points

- `AuthService.login/register` → `POST /api/v1/auth/login`, `POST /api/v1/auth/register`
- `SecurityEventService` → replace the simulated `interval()` feed with a `WebSocket` client
  against `/ws/security-events`, keeping the same signal/observable contract
- `ThreatService`, `RiskService`, `AlertService` → swap `of(mock)` for `HttpClient` calls once
  the Threat Detection Engine, Risk Intelligence Engine, and Alert Management REST endpoints
  are available. Interfaces in `core/models/security.model.ts` already match the intended
  backend contract.

## Design system

Dark "Cyber Intelligence Command Center" theme — void-black background, midnight-blue panels,
signal colors (green/amber/red/purple/cyan) for safe/warning/critical/AI-intelligence/live states.
JetBrains Mono for all metrics, timestamps and IDs; Inter for UI chrome. The signature visual
motif is the HUD corner-bracket panel (`.bracket` in `styles.css`) and the radial Risk
Constellation gauge, used in place of generic rounded cards and donut charts.
