# ADR-0010: Android Auto via AndroidX Car App Library

- **Status:** Accepted
- **Date:** 2026-06-30
- **Deciders:** ExpeditionGauge team

## Context

Drivers want live speed, G, and attitude on the head unit without proprietary Google automotive SDKs or a second telemetry pipeline.

## Decision

1. **`androidx.car.app:app`** (Apache 2.0) in isolated **`:car`** Gradle module — no Play Services in production path.
2. **`CarAppBridge`** registry — phone app implements bridge to `TelemetryBus` + `RecordingWriter`; car UI stays decoupled.
3. **`FeatureFlags.androidAutoEnabled`** defaults **false**; user opt-in via Settings.
4. **Metric allowlist** persisted in DataStore; default order per `CAR_GAUGE_PRIORITY.md`.
5. **PaneTemplate** (not custom OpenGL) for v2.3.0 — map/navigation deferred.

## Consequences

- Same APK ships phone + car entry points; DHU testing required for full AA certification path.
- OBD/BLE state visible on car rows when bridge exposes connected flags.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Google Play Services Car API | FOSS violation |
| Duplicate sensor pipeline in `:car` | Drift from phone fusion |
| WebView dashboard on car | AA template policy + latency |
