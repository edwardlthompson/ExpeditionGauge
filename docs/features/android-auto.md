# Feature: Android Auto integration

> Sprint 21 — car-display telemetry via AndroidX Car App Library (FOSS).

## Acceptance criteria

- `:car` module hosts `CarAppService` + `PaneTemplate` telemetry screen.
- Phone `TelemetryBus` feeds head unit via in-process `CarAppBridge` (no duplicate fusion).
- Settings toggle + metric allowlist; disabled = service no-op / empty pane message.
- Record start/stop/mark from car actions delegate to `RecordingWriter` on phone.
- Unit tests for `CarTelemetryHost` row mapping and allowlist order.

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
2. DHU / physical AA scenarios — manual when head unit or Desktop Head Unit available.
