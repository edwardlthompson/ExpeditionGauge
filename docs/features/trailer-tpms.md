# Feature: trailer-tpms

> Add four trailer axle TPMS ids beside the truck corners.

## Acceptance criteria

- ✅ Truck-only is FL FR RL RR
- ✅ Trailer adds T1L T1R T2L T2R
- ✅ i18n: none (ids)

## Smoke scenario

1. Given a 5th-wheel profile
2. When corners are listed
3. Then eight ids are present

## Container map

| Layer | Path |
|-------|------|
| Logic | `trailertpms/TrailerTpms.kt` |
| Tests | `app/src/test/.../trailertpms/` |

## Tests

- Automated: yes — `TrailerTpmsTest`
- Coverage: four vs eight

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
