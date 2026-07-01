# Feature: dual-orientation responsive HUD

> Sprint 20 — portrait + landscape dashboard with shared telemetry pipeline.

## Acceptance criteria

- App supports portrait and landscape on phone (`fullUser` orientation lock removed).
- Dashboard HUD switches layout via `OrientationLayoutEngine` (landscape row vs portrait column).
- Canvas gauges scale by orientation (`attitudeGaugeSizeDp`, `speedometerGaugeSizeDp`).
- Optional **Driving Mode** locks landscape while recording (DataStore preference).
- Rotation during recording does not stop session or disconnect BLE/OBD orchestrator.
- Gauge fusion math unchanged — layout-only sprint.

## Container map

| Layer | Path |
|-------|------|
| Spec | `docs/features/dual-orientation.md` |
| ADR | `docs/adr/0009-dual-orientation.md` |
| Layout engine | `ui/orientation/OrientationLayoutEngine.kt` |
| HUD variants | `DashboardHudLandscape.kt`, `DashboardHudPortrait.kt` |
| Driving pref | `settings/DrivingModePreferences.kt` |
| Services scope | `ExpeditionGaugeApplication.kt` |

## Smoke scenarios

1. Record in portrait → rotate landscape → Stop visible; session continues.
2. Cold start portrait → rotate → calibrate → record → open playback scrubber.
