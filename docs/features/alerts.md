# Feature: Configurable Alerts

> Sprint 13 — threshold alerts with haptic + audible feedback.

## Acceptance criteria

- ✅ User-defined thresholds: latG, drift, slip, pitch, roll, RPM, speed, fuel economy, TPMS
- ✅ Master toggle off by default
- ✅ 3 s cooldown per alert type prevents spam
- ✅ Haptic + ToneGenerator chime on fire
- ✅ AlertEventEntity logged during recording
- ✅ Playback scrubber markers for alerts

## Container map

| Layer | Path |
|-------|------|
| Logic | `alerts/AlertEngine.kt`, `alerts/AlertThresholds.kt` |
| Feedback | `alerts/AlertFeedback.kt` |
| Data | `AlertEventEntity` |
| Tests | `alerts/AlertEngineTest.kt` |

## Definition of Done

- FeatureFlags.alertsEnabled gates alert evaluation
- OBD-only alerts (RPM, fuel economy) skipped when OBD absent
