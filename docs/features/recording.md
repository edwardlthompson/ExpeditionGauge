# Recording + Export

> Sprint 6 — package `dev.foss.expeditiongauge.recording`, `dev.foss.expeditiongauge.export`

## Acceptance criteria

- ✅ Room v2 schema with stub tables (`TrackConfig`, `Lap`, `SectorSplit`, `AlertEvent`, `SessionEvent`, `SettingsProfile`)
- ✅ Nullable session metadata columns on `RecordingSessionEntity`
- ✅ `RecordingWriter` subscribes to `TelemetryBus.snapshots` (throttled to log interval)
- ✅ Logs β, `bodyYawDeg`, `velocityHeadingDeg`, `slipRatio`, fusion/IMU debug, TPMS, GPS in `extrasJson`
- ✅ CSV / JSON / GPX export via `ExportService` (β, slip, TPMS columns when present)
- ✅ Driver-first Record/Stop UI: red LIVE strip, full-width Stop, advanced options bottom sheet

## Container map

| Layer | Path |
|-------|------|
| DB | `data/db/ExpeditionGaugeDatabase.kt`, entities, DAOs |
| Writer | `recording/RecordingWriter.kt` |
| Export | `export/ExportService.kt`, `export/ExportExtrasParser.kt` |
| UI | `ui/components/gauge/RecordControls.kt`, `ui/recording/RecordingLiveStrip.kt` |

## Smoke scenario

1. Tap Record → red **● LIVE — recording** strip; HUD chrome minimized
2. Record 30 s → Stop (full-width red button)
3. Sessions list shows duration + peak speed; export CSV includes `driftAngleDeg`, `slipRatio`, optional `tpms_*` columns
