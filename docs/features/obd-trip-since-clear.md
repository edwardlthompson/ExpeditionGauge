# Feature: obd-trip-since-clear

> Mode 01 distance / warm-ups / time since DTCs cleared. Off the connect-timeout path.

## Acceptance criteria

- ✅ Parse `0131` km, `0130` warm-ups, `014E` minutes
- ✅ HUD status line `Since clear: …` when any PID is present
- ✅ `NO DATA` hides the line
- ✅ i18n: `obd_trip_*`

## Smoke scenario

1. Given Mode 01 returns 42 km and 3 warm-ups since clear
2. When the phone HUD is visible
3. Then a status line reads `Since clear: 42 km · 3 wu`

## Container map

| Layer | Path |
|-------|------|
| Logic | `obdtrip/ObdTrip.kt` |
| Adapter | `obd/Elm327ObdTrip.kt` |
| View | `ui/phonehuddtc/PhoneHudDtcFooter.kt` status lines |
| Tests | `src/test/.../obdtrip/` |
| Wiring | `ObdHudState` + `ObdPollLoop` |

## Tests

- Automated: yes — `ObdTripTest`
- Coverage: 4131 / 4130 / 414E parse; line join; NO DATA

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
