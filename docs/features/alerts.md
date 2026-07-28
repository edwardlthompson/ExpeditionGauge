# Feature: Configurable Alerts

> Sprint 13 — threshold alerts with haptic + audible feedback.
> Sprint 28 — Beep|TTS mode, 1 s level-triggered repeat, tire phrases, red/bold over-limit.

## Acceptance criteria

- User-defined thresholds: latG, drift, slip, pitch, roll, RPM, speed, fuel economy, TPMS
- Master toggle off by default
- While over limit: feedback every **1 s** (beep or TTS); stops when under limit
- User chooses **Beep** or **TTS** (phone locale via `Locale.getDefault()`)
- Mute silences audio only; active alert set and red/bold readouts continue
- Haptic on first edge into an alert; audio can repeat while over
- AlertEventEntity logged on edge during recording
- Playback scrubber markers for alerts
- Over-limit numbers: red + bold on phone HUD and AA telemetry cube

## Container map

| Layer | Path |
|-------|------|
| Logic | `alerts/AlertEngine.kt`, `alerts/AlertThresholds.kt`, `alerts/AlertService.kt` |
| Feedback | `alerts/AlertFeedback.kt`, `alerts/AlertTts.kt`, `alerts/AlertPhrases.kt` |
| Prefs | `accessibility/AudibleTones.kt` (`alertAudioMode`, `alertsMuted`) |
| Settings | `ui/settings/SettingsAlertOptions.kt` |
| Playback | `ui/playback/AlertSummaryPanel.kt`, scrubber `ALERT` markers |
| Tests | `alerts/AlertEngineTest.kt` |
## Definition of Done

- FeatureFlags.alertsEnabled gates alert evaluation
- OBD-only alerts (RPM, fuel economy) skipped when OBD absent
- AA Mute toggles same `alertsMuted` DataStore key as Settings
