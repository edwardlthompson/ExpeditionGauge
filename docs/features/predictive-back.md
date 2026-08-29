# Feature: predictive-back

> System back on feedback, permissions, comparison, session edit, and playback.

## Acceptance criteria

- ✅ Remaining routes are listed in `PredictiveBack`
- ✅ Those screens call `GaugeBackHandler`
- ✅ Playback back lives in `PlaybackBottomSection` so `PlaybackScreen` stays under 300 lines
- ✅ i18n: existing back labels

## Smoke scenario

1. Given playback or session compare
2. When the user swipes back
3. Then the previous screen opens

## Container map

| Layer | Path |
|-------|------|
| Logic | `predictiveback/PredictiveBack.kt` |
| Tests | `app/src/test/.../predictiveback/` |
| Wiring | remaining `*Screen` files |

## Tests

- Automated: yes — `PredictiveBackTest`
- Coverage: remaining route list

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
