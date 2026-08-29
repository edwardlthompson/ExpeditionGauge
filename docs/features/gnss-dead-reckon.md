# Feature: gnss-dead-reckon

> Keep a coarse position by integrating speed and heading when GNSS drops.

## Acceptance criteria

- ✅ North heading increases latitude
- ✅ Uses 111320 m per degree latitude
- ✅ i18n: none

## Smoke scenario

1. Given a fix at 0,0 and 10 m/s north
2. When one second elapses
3. Then latitude is greater than 0

## Container map

| Layer | Path |
|-------|------|
| Logic | `gnssdeadreckon/GnssDeadReckon.kt` |
| Tests | `app/src/test/.../gnssdeadreckon/` |

## Tests

- Automated: yes — `GnssDeadReckonTest`
- Coverage: north step

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
