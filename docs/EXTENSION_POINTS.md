# Extension Points — Core v1

> Sprint 8 — plug-in surfaces for v2+ without rewriting HUD/recording pipeline.

| Extension | Interface / hook | Sprint |
|-----------|------------------|--------|
| Telemetry source | `TelemetryBus.publish()` | 3+ |
| IMU parser | `WitMotionParser` / future vendor parsers | 4 |
| Multi-IMU fusion | `MultiImuYawFusion` | 4 |
| OBD transport | `ObdClassicManager` | 5 |
| Tire slip | `TireSlipCalculator` | 5 |
| TPMS parser | `TpmsParser` | 5b |
| GPS source | `FusedGpsLocationProvider` | 5c |
| Recording sink | `RecordingWriter` → Room | 6 |
| Export format | `ExportService` | 6 |
| Playback clock | `PlaybackEngine` | 7 |
| Live telemetry | `FeatureFlags.liveTelemetryEnabled` stub (default **false**) | 19 |

Core v1 keeps `liveTelemetryEnabled`, `tpmsEnabled`, and `externalGpsEnabled` runtime flags default **off**; user settings may enable TPMS/external GPS when hardware is present.

## Adding a new TPMS brand

1. Implement `TpmsParser` in `ble/tpms/`
2. Register in `BleTpmsManager` parser list
3. Add fixture hex under `ble/tpms/fixtures/`
4. Document in `docs/COMPATIBLE_HARDWARE.md`

## Adding a new export format

Extend `ExportService.exportSession()` with new `ExportFormat` enum value.

Live Telemetry (Sprint 19) wraps existing `TelemetryBus` — no sensor duplication.
