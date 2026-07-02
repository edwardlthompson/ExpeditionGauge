# ADR-0010: Android Auto via AndroidX Car App Library

- **Status:** Accepted (revised 2026-06-30)
- **Date:** 2026-06-30
- **Deciders:** ExpeditionGauge team

## Context

Drivers want live speed, G, and attitude on the head unit without proprietary Google automotive SDKs or a second telemetry pipeline. The phone HUD uses Compose/Canvas, which **cannot** be rendered on Android Auto head units.

## Decision

1. **`androidx.car.app:app`** (Apache 2.0) in isolated **`:car`** Gradle module — no Play Services in production path.
2. **`CarAppBridge`** registry — phone app implements bridge to `TelemetryBus` + `RecordingWriter`; car UI stays decoupled.
3. **Always-on when capable** — no Settings opt-out; `isAndroidAutoEnabled()` reflects build capability (`FeatureFlags.androidAutoCapable`), not a user toggle.
4. **`GridTemplate` with 3 tiles** — G-meter (pitch/roll/latG), telemetry (speed/HDG/alt), TPMS corners; structured text + icons only.
5. **Live refresh** — bridge rate-limits `Screen.invalidate()` to ~1 Hz while car session is active.
6. **Sideload discovery** — requires Android Auto developer mode + unknown sources (platform policy); documented in `docs/help/ANDROID_AUTO.md`.

## Consequences

- Same APK ships phone + car entry points; DHU testing required for full AA certification path.
- Metric allowlist removed; full cube metrics always shown on head unit (best-effort approximation).
- OBD/BLE/TPMS state visible when bridge exposes readings.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Google Play Services Car API | FOSS violation |
| Duplicate sensor pipeline in `:car` | Drift from phone fusion |
| WebView / custom OpenGL on car | AA template policy |
| Settings opt-in toggle | User confusion; service should be ready when host connects |
| PaneTemplate metric picker | Superseded by fixed 3-tile grid |
