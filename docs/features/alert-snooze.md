# Feature: alert-snooze

> Snooze one alert type for 5 minutes so it stops repeating feedback.

## Acceptance criteria

- ✅ Snoozed type skips audio/haptic until expiry
- ✅ Other types keep alerting
- ✅ Tap a selected chip to clear the snooze
- ✅ Expired or missing until → not suppressed
- ✅ i18n: `alerts_snooze_label`

## Smoke scenario

1. Given SPEED is over limit
2. When the driver taps the SPEED snooze chip
3. Then SPEED feedback stops for 5 minutes; other alerts still fire

## Container map

| Layer | Path |
|-------|------|
| Logic | `alertsnooze/AlertSnooze.kt` |
| Store | `settings/AlertSnoozeStore.kt` |
| View | `ui/alertsnooze/AlertSnoozeField.kt` |
| Tests | `src/test/.../alertsnooze/` |
| Wiring | AlertService feedback filter |

## Tests

- Automated: yes — `AlertSnoozeTest`
- Coverage: suppress window; encode/decode

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
