# Feature: battery-saver-record

> Optional recording profile that logs GPS-only samples at 5 Hz.

## Acceptance criteria

- ✅ Settings toggle persists in `BatterySaverRecordStore` (not `SettingsPreferences`)
- ✅ When enabled, Record uses 200 ms interval
- ✅ Stored samples drop IMU / G / attitude fields
- ✅ i18n: `battery_saver_record_toggle`

## Smoke scenario

1. Given battery-saver recording is on
2. When Record starts
3. Then samples write at 5 Hz without IMU extras

## Container map

| Layer | Path |
|-------|------|
| Logic | `batterysaverrecord/BatterySaverRecord.kt` |
| Store | `settings/BatterySaverRecordStore.kt` |
| View | `ui/batterysaverrecord/BatterySaverRecordField.kt` |
| Tests | `app/src/test/.../batterysaverrecord/` |
| Wiring | `DashboardViewModel.startRecording` + `RecordingSampleWriter` |

## Tests

- Automated: yes — `BatterySaverRecordTest`
- Coverage: inactive passthrough; GPS-only strip; interval apply

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
