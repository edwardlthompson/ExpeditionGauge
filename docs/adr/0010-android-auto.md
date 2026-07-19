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
4. **`NavigationTemplate` + Surface Drive HUD** — paint native **3×1** Attitude | Telemetry | TPMS on a full-bleed host `Surface` (`ACCESS_SURFACE` + `NAVIGATION_TEMPLATES` + `MAP_TEMPLATES`, Car API **7+**). No side content panel (MapWithContent left card removed). POI category kept for discovery (not a real map). Pane letterbox remains **fallback** only.
5. **Bitmap attitude family** — `DriveHudBitmapRenderer` paints inclinometer styles plus AA **G-meter** and **3D compass** bitmaps (`GMeterBitmapRenderer` / `CompassBallBitmapRenderer`). Tap the left cube to cycle the full phone `DISPLAY_CYCLE` (requires map **PAN** for Surface clicks). Telemetry cube includes compact lat/long.
6. **Top actions** — map ActionStrip **PAN only** (required for Surface taps). Main ActionStrip: titled **Screenshot** + **Record/Stop** + parked-only **Level** (title+icon; host collapses to icons on narrow screens). Pane fallback keeps max one titled action (Record).
7. **Live refresh** — Surface HUD paints at ~**30 Hz** (bridge 33 ms) without per-tick `Screen.invalidate()` (host template rebuilds stay rare). Pane fallback still throttles template invalidate to **500 ms**.
8. **Sideload discovery** — `CarAppService` declares a **single** `androidx.car.app.category.POI` category (not IOT, not dual categories, not NAVIGATION). Many real projected head units filter IOT even with Unknown sources; DHU is more permissive. POI is the least-wrong AA-allowed category for this FOSS telemetry grid — **not Play-certification-ready**. Also requires Android Auto developer mode + unknown sources + Customize launcher (platform policy); documented in `docs/help/ANDROID_AUTO.md`.
9. **HostValidator** — `ALLOW_ALL_HOSTS_VALIDATOR` for sideload/DHU/MITM adapters; stricter validators deferred until a Play path exists.
10. **Non-AA dash routes** — same APK also targets aftermarket Android HUs and AAOS sideload (soft hardware features, distraction-optimized `MainActivity`); see `docs/help/HEAD_UNIT_ROUTES.md`.
11. **Bridge crash contract** — `CarAppBridge` mutators never throw into host click callbacks (Car App Library only catches `RuntimeException`; checked failures like `StorageCapBlockedException` were fatal). Implement with `runCatching` + async IO; toast via `CarToast` on failure.
12. **Bitmap isolation** — size-keyed renderer pools; always hand an immutable `Bitmap.copy` to `CarIcon` / Compose so phone Offroad and AA never share a live canvas buffer. Pane HUD width caps at `AaDisplaySpec.MAX_PANE_BITMAP_PX` (3 × 360).
13. **Sensor hold** — refcounted `acquireSensors` / `releaseSensors` so an active AA session keeps IMU/GPS/BLE alive after Activity `onStop`.
14. **`minCarApiLevel` 7** — required for `MapWithContentTemplate`.
15. **AA screenshot** — `CarAppBridge.captureAaScreenshot()` saves the last native 3×1 Drive HUD bitmap via MediaStore (`Pictures/ExpeditionGauge`).
16. **Surface review risk** — HUD-on-map-surface may fail future Play AA review; FOSS sideload is the primary channel.

## Consequences

- Same APK ships phone + car entry points; Play listing out of scope until an honest category + templates exist.
- After category/APK upgrades, force-stop (and preferably clear) Android Auto so the host rediscovers services — see `aa-refresh-host.ps1`.
- DHU/head-unit validation remains M-003; if Customize launcher stays empty after POI + reset, escalate OEM/cable only — do not churn categories.
- Phone **Offroad** preset selects `AttitudeGaugeMode.INCLINOMETER` using the same renderer family at phone max size (separate pool entry / copy).
- Pitch/roll angle alerts reuse Settings thresholds; optional Alert row on Pane when active.
- Pane rows are host-laid-out text (not multi-line GridItem hacks).
- Session-locked display spec — re-resolve only on HU `onCarConfigurationChanged`.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Google Play Services Car API | FOSS violation |
| Duplicate sensor pipeline in `:car` | Drift from phone fusion |
| WebView / custom OpenGL on car | AA template policy |
| MapTemplate / live map tiles | Not needed; Surface is used only as a free canvas for gauges |
| Pane `setImage` as primary | Host square box letterboxes/crops; kept as fallback only |
| `GridTemplate` 3 LARGE tiles | Rejected for HUD look / size |
| Settings opt-in toggle | User confusion; service should be ready when host connects |
| Fixed 3-tile `GridTemplate` | Host ~128 dp thumbs too small for glanceable HUD (superseded by Pane Drive HUD) |
| `GridTemplate.setItemSize(LARGE)` only | Still multi-thumb; Pane ~480 dp image is the real size jump |
| `category.IOT` for discovery | Filtered by many projected head units (DHU more permissive); use single `POI` for FOSS sideload |
| Dual categories / NAVIGATION | Confuses hosts; NAVIGATION adds map obligations; Play path still out of scope |
