# Feature: obd-temps-voltage

> Parked/idle coolant, oil, and battery cluster. Off the connect-timeout path.

## Acceptance criteria

- ✅ Parse `0105` ECT (°C = A−40), `015C` oil (°C = A−40), `0142` battery V
- ✅ HUD status line when parked (speed &lt; 0.5 m/s or unknown) and any value is present
- ✅ Hidden while moving; `NO DATA` hides the field
- ✅ i18n: `obd_temps_voltage_*`

## Smoke scenario

1. Given Mode 01 returns ECT 90 °C and 13.8 V while speed is 0
2. When the phone HUD is visible
3. Then the status line shows ECT and Batt

## Container map

| Layer | Path |
|-------|------|
| Logic | `obdtemps/ObdTempsVoltage.kt` |
| Adapter | `obd/Elm327TempsVoltage.kt` |
| View | HUD `statusLines` |
| Tests | `src/test/.../obdtemps/` |
| Wiring | `ObdHudState` + scan tick |

## Tests

- Automated: yes — `ObdTempsVoltageTest`
- Coverage: parse; parked vs moving; NO DATA

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
