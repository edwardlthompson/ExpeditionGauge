# Feature: mag-hardiron

> Fit a hard-iron offset from a short magnetometer figure-eight.

## Acceptance criteria

- ✅ Needs at least 8 samples
- ✅ Offset is midpoint of min/max per axis
- ✅ Apply subtracts the offset
- ✅ i18n: none (wizard math)

## Smoke scenario

1. Given eight mag samples spanning a bias
2. When the wizard fits
3. Then applying the offset centers the cloud

## Container map

| Layer | Path |
|-------|------|
| Logic | `maghardiron/MagHardIron.kt` |
| Tests | `app/src/test/.../maghardiron/` |
| Wiring | Calibration mag path can call `fit` |

## Tests

- Automated: yes — `MagHardIronTest`
- Coverage: fit; apply; too few

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
