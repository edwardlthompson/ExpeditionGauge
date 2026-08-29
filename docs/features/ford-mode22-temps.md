# Feature: ford-mode22-temps

> Poll catalog TFT / EGT Mode 22 PIDs onto the HUD. Off the connect-timeout path.

## Acceptance criteria

- ✅ After a DTC-scan tick, probe TRANS_TEMP then EGT with PCM header `7E0`
- ✅ HUD status line `TFT 82°C · EGT 410°C` when either value parses
- ✅ `NO DATA` hides the line
- ✅ i18n: `ford_mode22_temps_*`

## Smoke scenario

1. Given a Ford ECU answers `221E1C`
2. When the phone HUD is visible
3. Then a status line shows TFT in °C

## Container map

| Layer | Path |
|-------|------|
| Logic | `fordmode22/FordMode22Temps.kt` |
| Adapter | `obd/Elm327FordMode22.kt` |
| View | HUD `statusLines` |
| Tests | `src/test/.../fordmode22/FordMode22TempsTest.kt` |
| Wiring | `ObdHudState` + `ObdPollLoop` scan |

## Tests

- Automated: yes — `FordMode22TempsTest`
- Coverage: both temps; TFT only; empty

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
