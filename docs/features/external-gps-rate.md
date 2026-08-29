# Feature: external-gps-rate

> Clamp external GPS baud and NMEA Hertz to supported values.

## Acceptance criteria

- ✅ Baud snaps to the nearest of 4800–115200
- ✅ Rate stays 1–20 Hz
- ✅ i18n: none

## Smoke scenario

1. Given baud 10000 and 99 Hz
2. When clamped
3. Then baud is 9600 and rate is 20 Hz

## Container map

| Layer | Path |
|-------|------|
| Logic | `externalgpsrate/ExternalGpsRate.kt` |
| Tests | `app/src/test/.../externalgpsrate/` |

## Tests

- Automated: yes — `ExternalGpsRateTest`
- Coverage: baud snap; hz clamp

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
