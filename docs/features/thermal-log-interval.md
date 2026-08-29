# Feature: thermal-log-interval

> While recording, log interval follows thermal status automatically.

## Acceptance criteria

- ✅ Recording + Warning → 50 ms; Critical → 200 ms
- ✅ Not recording → no change
- ✅ Battery-saver stays at 200 ms even on Warning
- ✅ i18n: none (interval only)

## Smoke scenario

1. Given a session is recording and the phone hits thermal warning
2. When thermal status refreshes
3. Then log interval becomes 50 ms

## Container map

| Layer | Path |
|-------|------|
| Logic | `thermalloginterval/ThermalLogInterval.kt` |
| Tests | `app/src/test/.../thermalloginterval/` |
| Wiring | `ExpeditionGaugeServiceRegistry.bindLifecycleFlows` |

## Tests

- Automated: yes — `ThermalLogIntervalTest`
- Coverage: idle skip; warning interval; battery-saver floor

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
