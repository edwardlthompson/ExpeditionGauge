# Feature: thermal-record-ui

> HUD chip suggests a lower log rate when the phone is thermally limited.

## Acceptance criteria

- ✅ Warning → 20 Hz suggestion; Critical → 5 Hz
- ✅ Tap applies the interval through Settings (writer follows the flow)
- ✅ Normal thermal status hides the banner
- ✅ i18n: `thermal_record_apply_hz`

## Smoke scenario

1. Given the phone reports thermal warning
2. When the HUD banner apply button is tapped
3. Then log interval becomes 50 ms

## Container map

| Layer | Path |
|-------|------|
| Logic | `thermalrecord/ThermalRecordThrottle.kt` |
| View | `ui/thermalrecord/ThermalRecordBanner.kt` |
| Tests | `app/src/test/.../thermalrecord/` |
| Wiring | `DashboardScreen` thermal block |

## Tests

- Automated: yes — `ThermalRecordThrottleTest`
- Coverage: status → interval and Hz label

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
