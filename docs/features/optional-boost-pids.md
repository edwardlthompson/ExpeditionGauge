# Feature: optional-boost-pids

> Optional Mode 01 MAP / AFR / boost. Off the connect-timeout path.

## Acceptance criteria

- ✅ Parse `010B` MAP kPa, `0134` AFR (λ×14.7), boost = MAP − `0133` baro when positive
- ✅ HUD status line when any value is present
- ✅ `NO DATA` hides the line
- ✅ i18n: `boost_pids_*`

## Smoke scenario

1. Given Mode 01 returns MAP 120 kPa and baro 100 kPa
2. When the phone HUD is visible
3. Then the status line shows MAP and Boost

## Container map

| Layer | Path |
|-------|------|
| Logic | `boostpids/BoostPids.kt` |
| Adapter | `obd/Elm327BoostPids.kt` |
| View | HUD `statusLines` |
| Tests | `src/test/.../boostpids/` |
| Wiring | `ObdHudState` + scan tick |

## Tests

- Automated: yes — `BoostPidsTest`
- Coverage: MAP/AFR/boost math; line; NO DATA

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
