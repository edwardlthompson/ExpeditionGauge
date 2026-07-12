# ADR-0010: Android Auto via AndroidX Car App Library

- **Status:** Accepted (revised 2026-07-11)
- **Date:** 2026-06-30
- **Deciders:** ExpeditionGauge team

## Context

Drivers want live speed and attitude on the head unit without proprietary Google automotive SDKs or a second telemetry pipeline. The phone HUD uses Compose/Canvas, which **cannot** be rendered on Android Auto head units.

## Decision

1. **`androidx.car.app:app`** (Apache 2.0) in isolated **`:car`** Gradle module — no Play Services in production path.
2. **`CarAppBridge`** registry — phone app implements bridge to `TelemetryBus` + `RecordingWriter` + `CalibrationStore`; car UI stays decoupled.
3. **Always-on when capable** — no Settings opt-out; `isAndroidAutoEnabled()` reflects build capability (`FeatureFlags.androidAutoCapable`), not a user toggle.
4. **`GridTemplate` with 3 tiles** — **Attitude** (bitmap inclinometer ±45° + pitch/roll text), telemetry (speed/HDG/alt), TPMS (text corners only). **No maps.**
5. **Bitmap inclinometer** — `CarIcon` from shared `:car` `InclinometerBitmapRenderer` (progressive green→yellow→red by angle). Not a custom OpenGL surface.
6. **Action strip** — **Record / Stop** and **Zero** (set level); mark-event and advanced features stay on phone.
7. **Live refresh** — bridge rate-limits `Screen.invalidate()` to **250 ms** app-side (host may cap lower).
8. **Sideload discovery** — `CarAppService` declares a **single** `androidx.car.app.category.POI` category (not IOT, not dual categories, not NAVIGATION). Many real projected head units filter IOT even with Unknown sources; DHU is more permissive. POI is the least-wrong AA-allowed category for this FOSS telemetry grid — **not Play-certification-ready**. Also requires Android Auto developer mode + unknown sources + Customize launcher (platform policy); documented in `docs/help/ANDROID_AUTO.md`.
9. **HostValidator** — `ALLOW_ALL_HOSTS_VALIDATOR` for sideload/DHU/MITM adapters; stricter validators deferred until a Play path exists.
10. **Non-AA dash routes** — same APK also targets aftermarket Android HUs and AAOS sideload (soft hardware features, distraction-optimized `MainActivity`); see `docs/help/HEAD_UNIT_ROUTES.md`.

## Consequences

- Same APK ships phone + car entry points; Play listing out of scope until an honest category + templates exist.
- After category/APK upgrades, force-stop (and preferably clear) Android Auto so the host rediscovers services — see `aa-refresh-host.ps1`.
- DHU/head-unit validation remains M-003; if Customize launcher stays empty after POI + reset, escalate OEM/cable only — do not churn categories.
- Phone **Offroad** preset selects `AttitudeGaugeMode.INCLINOMETER` using the same renderer at full size.
- Pitch/roll angle alerts reuse Settings thresholds; red frame on AA attitude tile when active.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Google Play Services Car API | FOSS violation |
| Duplicate sensor pipeline in `:car` | Drift from phone fusion |
| WebView / custom OpenGL on car | AA template policy |
| MapTemplate / live map on AA | Navigation scope; not needed for telemetry HUD |
| Settings opt-in toggle | User confusion; service should be ready when host connects |
| PaneTemplate metric picker | Superseded by fixed 3-tile grid |
| `category.IOT` for discovery | Filtered by many projected head units (DHU more permissive); use single `POI` for FOSS sideload |
| Dual categories / NAVIGATION | Confuses hosts; NAVIGATION adds map obligations; Play path still out of scope |
