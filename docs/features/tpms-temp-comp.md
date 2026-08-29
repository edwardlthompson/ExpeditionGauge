# Feature: tpms-temp-comp

> Scale TPMS pressure back to 20 °C with the ideal-gas law.

## Acceptance criteria

- ✅ Hotter than 20 °C lowers reported kPa
- ✅ At 20 °C the value is unchanged
- ✅ i18n: none

## Smoke scenario

1. Given 240 kPa at 50 °C
2. When compensated
3. Then the value is below 240 kPa

## Container map

| Layer | Path |
|-------|------|
| Logic | `tpmstempcomp/TpmsTempComp.kt` |
| Tests | `app/src/test/.../tpmstempcomp/` |

## Tests

- Automated: yes — `TpmsTempCompTest`
- Coverage: hot vs ref

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
