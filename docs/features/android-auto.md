# Feature: Android Auto integration

> Sprint 21 — car-display telemetry via AndroidX Car App Library (FOSS).

## Acceptance criteria

- `:car` module hosts `CarAppService` + **`NavigationTemplate` full-bleed Surface Drive HUD** (native 3×1 Attitude | Telemetry | TPMS) — see ADR-0010.
- Phone `TelemetryBus` feeds head unit via in-process `CarAppBridge` (no duplicate fusion).
- `DriveHudSurfacePainter` draws the 3×1 strip onto the host Surface; Pane letterbox is fallback only.
- Actions: Screenshot + Record/Stop + parked-only Zero; mark-event stays on phone.
- Preview: `dhu-preview.ps1` (Desktop Head Unit CLI) and `aa-bitmap-preview.ps1` (PNG snapshots for Cursor).
- Unit tests for tile builders, glance icons, and `CarTelemetryHost` metrics.

## Metric priority (default)

See [`docs/design/CAR_GAUGE_PRIORITY.md`](../design/CAR_GAUGE_PRIORITY.md): speed, latG, pitch, roll, β, RPM/throttle.

## Container map

| Layer | Path |
|-------|------|
| Spec | `docs/features/android-auto.md` |
| ADR | `docs/adr/0010-android-auto.md` |
| Car module | `examples/android/car/` |
| Bridge (app) | `car/AndroidAutoBridge.kt` |
| Settings UI | `ui/settings/SettingsAndroidAutoOptions.kt` |
## Smoke scenarios

1. `aa-service-registered` — CarAppService in package manager after install.
2. DHU / physical AA — `pwsh scripts/expedition/dhu-preview.ps1` or vehicle HU (M-003).
3. Bitmap preview — `pwsh scripts/expedition/aa-bitmap-preview.ps1` → `.cursor/screenshots/aa-tile-*.png`.
