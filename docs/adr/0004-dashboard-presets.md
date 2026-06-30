# ADR-0004: Dashboard Presets as Declarative Layout Config

- **Status:** Accepted
- **Date:** 2026-06-29
- **Deciders:** ExpeditionGauge team

## Context

Drivers need quick layout switches (Drift, Offroad, Track, Minimal) without duplicating ViewModels or forking fusion logic. Presets must persist per vehicle profile and apply during recording without interrupting sensor fusion.

## Decision

1. **`DashboardPreset`** — sealed enum-like model with panel weights, visibility flags, and linked `RecordingMode` (`NORMAL` | `CRAWLING`).
2. **`SettingsProfile`** — domain model backed by Room `SettingsProfileEntity` + DataStore active profile id.
3. **Single `DashboardViewModel`** reads active preset from profile store; UI applies `Row(Modifier.weight())` from preset config only.
4. **`FeatureFlags`** gates optional panels (TPMS, drift readout, live telemetry) — core fusion path unchanged when flags off.

## Consequences

- Preset switch is O(1) state update; no ViewModel recreation.
- Offroad preset links to `CRAWLING` recording profile automatically.
- New presets add enum entry + JSON migration in profile config — no screen duplication.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Separate screen per preset | Duplicates gauge wiring and ViewModels |
| Runtime Compose `when` without persistence | Lost on rotation / app restart |
| Hard-coded layout in DashboardScreen | Cannot save per-vehicle profiles |
