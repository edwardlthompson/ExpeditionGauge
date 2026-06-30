# Recording + Export

> Sprint 6 — package `dev.foss.expeditiongauge.recording`, `dev.foss.expeditiongauge.export`

## Acceptance criteria

- ✅ Room v1 schema with stub tables (TrackConfig, Lap, SectorSplit, AlertEvent, SessionEvent, SettingsProfile)
- ✅ `RecordingWriter` subscribes to TelemetryBus (not raw sensors)
- ✅ Logs β, slipRatio, TPMS, GPS metadata in `extrasJson`
- ✅ CSV / JSON / GPX export via `ExportService`
- ✅ Record/Stop controls on dashboard

## Container map

| Layer | Path |
|-------|------|
| DB | `recording/RecordingDatabase.kt`, entities, DAOs |
| Writer | `recording/RecordingWriter.kt` |
| Export | `export/ExportService.kt` |
| UI | `ui/components/gauge/RecordControls.kt` |

## Smoke scenario

1. Tap Record on dashboard → LIVE strip
2. Drive 2 min → Stop
3. Sessions list shows entry; export JSON includes driftAngleDeg and slipRatio columns
