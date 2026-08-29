# Feature: aa-parked-dtc

> Parked Android Auto Drive HUD opens a stored-DTC list when the footer band is tapped.

## Acceptance criteria

- ✅ Tap the ROW DTC footer while parked with codes → ListTemplate of code + title
- ✅ Moving, no codes, or COLUMN layout does not open the pane
- ✅ Driving after open shows “Park to read codes”
- ✅ i18n: none (OBD codes stay English catalog titles)

## Smoke scenario

1. Given the vehicle is parked and two stored DTCs exist
2. When the driver taps the red DTC footer
3. Then AA shows P-codes with catalog titles and Back returns to Drive

## Container map

| Layer | Path |
|-------|------|
| Logic | `car/aaparkeddtc/AaParkedDtc.kt` |
| View | `car/ui/AaParkedDtcScreen.kt` |
| Tests | `car/src/test/.../aaparkeddtc/` |
| Wiring | footer tap + `CarAppBridge.parkedDtcRows` |

## Tests

- Automated: yes — `AaParkedDtcTest`, footer hit-test
- Coverage: parked+count gate; row cap; empty description fallback

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
