# Feature: vehicle-pid-maps

> Keep Mode 01 PID hex lists per vehicle, defaulting to RPM/speed/coolant.

## Acceptance criteria

- ✅ Default is 0C 0D 05
- ✅ Parse drops non-hex tokens
- ✅ Unknown vehicle uses default
- ✅ i18n: none

## Smoke scenario

1. Given blob `11, 0c, zz`
2. When parsed
3. Then PIDs are 11 and 0C

## Container map

| Layer | Path |
|-------|------|
| Logic | `vehiclepidmaps/VehiclePidMaps.kt` |
| Tests | `app/src/test/.../vehiclepidmaps/` |

## Tests

- Automated: yes — `VehiclePidMapsTest`
- Coverage: parse; fallback

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
