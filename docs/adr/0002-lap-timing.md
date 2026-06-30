# ADR-0002: Lap Timing Architecture

- **Status:** Accepted
- **Date:** 2026-06-29
- **Deciders:** ExpeditionGauge team

## Context

Lap and sector timing must work phone-only (GPS only) and integrate with playback without desyncing gauges, map, and graphs.

## Decision

1. **`PlaybackEngine`** is the single scrubber clock for all playback consumers (gauges, map, graphs, ghost overlay).
2. **Lap timing is pure GPS domain** — `LapDetector`, `SectorSplitCalculator`, and `PredictiveTimingEngine` operate on `SampleEntity` coordinates only. No MapLibre or map SDK coupling in timing logic.
3. **`TrackConfigEntity`** stores start/finish and sector lines as GeoJSON strings; parsed to `LineSegment` in Kotlin.
4. **Crossing detection** uses 2D segment intersection with minimum speed threshold (2 m/s default) to reject GPS jitter at low speed.

## Consequences

- Timing logic is unit-testable with fixture GPS tracks (no Android/map dependencies)
- Map UI can display sector boundaries independently by reading the same GeoJSON
- Indoor/no-GPS sessions skip lap features gracefully

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| MapLibre hit-testing for crossings | Couples timing to map SDK; harder to test |
| Separate scrubbers per panel | Causes playback desync (see BUILD_PLAN risk table) |
| Transponder/beacon hardware | Out of scope for v1 polish |
