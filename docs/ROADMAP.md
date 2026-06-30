# ExpeditionGauge Roadmap

> Core v1 (Sprints 0–8) ships phone-only HUD, recording, export, and MapLibre playback. Polish and v2 sprints are opt-in via `project.config.json`.

## Shipped — v2.0.0 (2026-06-30)

- Video sync — import MP4, offset UI, ExoPlayer preview on playback
- MediaCodec telemetry burn-in export
- Full calibration wizard + Test Drive step
- Developer / Advanced mode (Madgwick β tuning, off by default)
- Enhanced export — GPX extensions, session ZIP bundle

## Shipped — Core v1 (1.2.0)

- Automotive HUD: attitude G-meter, speed, heading, drift angle β
- Optional hardware: BLE IMU, OBD-II, BLE TPMS, external Bluetooth GPS (all default off)
- Session recording + CSV/JSON/GPX export
- Playback with β-colored route, drift analysis overlay, elevation profile
- Settings: units, log rate, device management, calibration reset
- F-Droid-ready FOSS stack (no Play Services / Firebase)
- Polish wave 1 (v1.1): metadata, crawl, laps, graphs, heatmaps, ghost lap, alerts
- Polish waves 2–3 (v1.2): presets, playback layout, stats, mark events, onboarding, accessibility

## Shipped — Core v1 (1.1.0) — superseded by 1.2.0

## Wave 1 Polish (Sprints 9–14) — `sprints.wave1_polish`

| Sprint | Theme |
|--------|-------|
| 9 | Session metadata, crawling mode, tags/photos |
| 10 | Lap / sector timing, predictive delta |
| 11 | Telemetry graphs, ghost lap |
| 12 | Driving line overlay, heatmap polish |
| 13 | Alerts engine |
| 14 | Dashboard presets, lap timer strip |

## Wave 2 Polish (Sprints 15–18) — `sprints.wave2_polish`

Accessibility, i18n, high-contrast, audible tones, video export stub.

## v2 — Live Telemetry (Sprint 19) — `sprints.v2_live_telemetry`

`FeatureFlags.liveTelemetryEnabled` remains **false** in core v1. Sprint 19 adds opt-in LAN/WebRTC relay over existing `TelemetryBus` — no sensor duplication.

## Deferred / out of scope

- Cloud accounts, proprietary maps, Play Services
- GPL runtime deps (PECHAM parser remains reference-only stub)

Track sprint status in [`BUILD_PLAN.md`](../BUILD_PLAN.md) and archive in [`COMPLETED_TASKS.md`](../COMPLETED_TASKS.md).
