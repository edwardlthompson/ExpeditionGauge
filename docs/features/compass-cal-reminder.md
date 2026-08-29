# Feature: compass-cal-reminder

> Ask for a compass recalibration after a large magnetometer jump.

## Acceptance criteria

- ✅ Spike ≥ 40 µT triggers a reminder
- ✅ Small changes do not
- ✅ Magnitude helper is 3-axis RMS
- ✅ i18n: none (logic)

## Smoke scenario

1. Given a stable 50 µT field
2. When the next sample is 95 µT
3. Then a calibration reminder is due

## Container map

| Layer | Path |
|-------|------|
| Logic | `compasscalreminder/CompassCalReminder.kt` |
| Tests | `app/src/test/.../compasscalreminder/` |
| Wiring | `MagHardIron.remember` callers can consult `shouldRemind` |

## Tests

- Automated: yes — `CompassCalReminderTest`
- Coverage: spike vs quiet

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
