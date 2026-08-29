# Feature: haptic-alerts

> Repeat haptic feedback on every over-limit alert tick. Off by Settings toggle.

## Acceptance criteria

- ✅ Over-limit feedback ticks vibrate when the haptic toggle is on (default on)
- ✅ Toggle off → no vibration
- ✅ Under-limit → no vibration
- ✅ Mute still silences audio only
- ✅ i18n: `alerts_haptic_toggle`

## Smoke scenario

1. Given alerts are enabled and a threshold is exceeded
2. When feedback repeats on the cooldown
3. Then the device vibrates each tick until the value drops under the limit

## Container map

| Layer | Path |
|-------|------|
| Logic | `hapticalerts/HapticOverLimit.kt` |
| Store | `settings/HapticAlertsStore.kt` |
| View | `ui/hapticalerts/HapticAlertsField.kt` |
| Tests | `src/test/.../hapticalerts/` |
| Wiring | AlertService + Settings alerts |

## Tests

- Automated: yes — `HapticOverLimitTest`
- Coverage: enabled/disabled × over/under limit

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
