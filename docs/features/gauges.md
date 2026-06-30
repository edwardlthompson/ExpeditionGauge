# Gauges — Sprint 2 Acceptance

Visual contract: [`docs/design/GAUGE_REFERENCE.md`](../design/GAUGE_REFERENCE.md) (layout, palette, calibration, deviations).

## Layout

Landscape three-panel `Row` per [GAUGE_REFERENCE § Layout](../design/GAUGE_REFERENCE.md#layout):

| Left | Center | Right |
|------|--------|-------|
| `AttitudeGMeterGauge` | `SpeedometerGauge` + `HeadingReadout` + `GpsReadoutPanel` | `TirePressurePanel` + `StatusIcons` |

## Logic

| Module | Path | Role |
|--------|------|------|
| Ball mapping | `gauge/AttitudeBallLogic.kt` | Pitch/roll → normalized ball offset + color zone |
| G-force stub | `gauge/GForceBallLogic.kt` | Sprint 11 hybrid mode placeholder |
| Shared helpers | `gauge/GaugeLogic.kt` | Formatting, ring thresholds |
| Calibration | `calibration/CalibrationStore.kt` | DataStore pitch/roll zero offsets |

## Acceptance criteria

- Three-panel HUD matches GAUGE_REFERENCE palette (`gaugeGreen`, `gaugeYellow`, `gaugeRed`, `gaugeScaleWhite`, `gaugeBall`).
- Attitude panel shows concentric 10°/20°/30° rings, crosshairs, animated ball, Pitch/Roll readouts.
- **Calibrate / Set Level** zeros ball at level surface via `CalibrationStore`.
- Center panel: large white speed digits, yellow unit label, numeric HDG only (no compass dial).
- Right panel: FL/FR/RL/RR with `--` when no TPMS data.
- `DashboardViewModel` drives UI from `TelemetryBus` (mock or live).
- Unit tests pass for `AttitudeBallLogic` ball clamping and zone thresholds.

## Deviations (intentional)

See GAUGE_REFERENCE.md — ball-in-ring left panel replaces reference vertical inclinometer bars; tire panel replaces compass dial.
