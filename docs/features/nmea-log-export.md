# Feature: nmea-log-export

> Keep the last 2000 raw NMEA sentences and share them as a `.nmea` file.

## Acceptance criteria

- ✅ External GPS read loop retains `$` sentences
- ✅ Buffer evicts oldest lines past 2000
- ✅ Settings Hardware can share the log
- ✅ i18n: `nmea_log_share`

## Smoke scenario

1. Given an external GPS is streaming NMEA
2. When Share NMEA log is tapped
3. Then a text file of recent sentences is offered to the system share sheet

## Container map

| Layer | Path |
|-------|------|
| Logic | `nmealogexport/NmeaLogExport.kt` |
| View | `ui/nmealogexport/NmeaLogShareButton.kt` |
| Tests | `app/src/test/.../nmealogexport/` |
| Wiring | `ExternalNmeaGpsManager` + `SettingsHardwareOptions` |

## Tests

- Automated: yes — `NmeaLogExportTest`
- Coverage: ignore non-NMEA; evict to max

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
