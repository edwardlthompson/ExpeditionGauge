# ADR-0009: Dual-Orientation Responsive HUD

- **Status:** Accepted
- **Date:** 2026-06-30
- **Deciders:** ExpeditionGauge team

## Context

Sprint 19b fixed navigation insets for edge-to-edge. Users want portrait pocket use and landscape windshield mount without forking telemetry or recording pipelines.

## Decision

1. Remove forced `sensorLandscape`; use **`fullUser`** with `configChanges` so Compose recomposes without Activity recreation where possible.
2. **`OrientationLayoutEngine`** maps window dp size → layout spec (gauge diameters, compact GPS flag).
3. **Separate composables** `DashboardHudLandscape` / `DashboardHudPortrait` — shared telemetry props, no duplicated fusion logic.
4. **`DrivingModePreferences`** (opt-in): lock landscape while recording via `Activity.requestedOrientation`.
5. **`ExpeditionGaugeApplication`** owns **`ExpeditionGaugeServices`** singleton — survives configuration changes.

## Consequences

- Playback and settings screens inherit orientation unlock; dashboard is primary layout target.
- Tablet / foldables can extend specs later without changing telemetry bus.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Single layout with weights only | Portrait unusable — gauges too small |
| Separate Activities per orientation | Breaks recording continuity |
| Force landscape always | Blocks Sprint 20 product goal |
