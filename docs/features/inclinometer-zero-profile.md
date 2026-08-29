# Feature: inclinometer-zero-profile

> Keep a separate pitch/roll zero for each vehicle id.

## Acceptance criteria

- ✅ Keys normalize to `zero:{id}`
- ✅ Encode is `pitch|roll`
- ✅ Parse a `;` map of vehicle offsets
- ✅ i18n: none

## Smoke scenario

1. Given vehicle id Truck
2. When Zero is saved as 1.5|-0.5
3. Then decode returns that offset

## Container map

| Layer | Path |
|-------|------|
| Logic | `inclinometerzeroprofile/InclinometerZeroProfile.kt` |
| Tests | `app/src/test/.../inclinometerzeroprofile/` |

## Tests

- Automated: yes — `InclinometerZeroProfileTest`
- Coverage: key; encode; parseAll

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
