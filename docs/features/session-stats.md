# Feature: Session Stats Dashboard

> Sprint 17 — local Room aggregates and rich session cards.

## Acceptance criteria

- ✅ `SessionStatsAggregator` computes peak latG, max |β|, slip events, marked events, best lap
- ✅ Stats dashboard with aggregate header (session count, total duration, fleet best lap)
- ✅ Rich session cards: route spark thumbnail, Play / Compare / Share summary
- ✅ Session list reuses rich cards when stats loaded
- ✅ `FeatureFlags.sessionStatsEnabled`

## Container map

| Layer | Path |
|-------|------|
| Logic | `stats/SessionStatsAggregator.kt` |
| UI | `ui/stats/SessionStatsDashboard.kt`, `ui/stats/RichSessionCard.kt` |
| Wiring | `ExpeditionGaugeApp.kt` (LaunchedEffect load), `AppScreenSessionRoutes.kt` |
