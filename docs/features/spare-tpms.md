# Feature: spare-tpms

> Optional fifth TPMS corner for a spare tire.

## Acceptance criteria

- ✅ Default map is FL FR RL RR
- ✅ Include spare adds SPARE
- ✅ i18n: none (ids)

## Smoke scenario

1. Given spare TPMS is enabled
2. When corners are listed
3. Then SPARE is fifth

## Container map

| Layer | Path |
|-------|------|
| Logic | `sparetpms/SpareTpms.kt` |
| Tests | `app/src/test/.../sparetpms/` |

## Tests

- Automated: yes — `SpareTpmsTest`
- Coverage: four vs five

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
