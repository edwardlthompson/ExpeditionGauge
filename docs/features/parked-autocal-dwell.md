# Feature: parked-autocal-dwell

> Require 5 s of stillness when parked before proposing autocal.

## Acceptance criteria

- ✅ Moving hold stays 2.5 s
- ✅ Parked hold is 5 s
- ✅ Still detector default uses the moving hold
- ✅ i18n: none

## Smoke scenario

1. Given the phone is parked
2. When autocal watches stillness
3. Then it waits 5 seconds before proposing Zero

## Container map

| Layer | Path |
|-------|------|
| Logic | `parkedautocaldwell/ParkedAutocalDwell.kt` |
| Tests | `app/src/test/.../parkedautocaldwell/` |
| Wiring | `StationaryStillDetector` default hold |

## Tests

- Automated: yes — `ParkedAutocalDwellTest`
- Coverage: parked vs moving

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
