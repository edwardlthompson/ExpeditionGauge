# Feature: keep-awake-moving

> Keep the screen awake only while the vehicle is moving.

## Acceptance criteria

- ✅ Settings keep-awake off → screen may time out
- ✅ Keep-awake on + speed ≥ 0.5 m/s → FLAG_KEEP_SCREEN_ON
- ✅ Keep-awake on + parked or unknown speed → allow timeout
- ✅ i18n: none (reuses existing keep-awake toggle)

## Smoke scenario

1. Given keep-awake is enabled and the vehicle is moving
2. When the HUD is visible
3. Then the screen stays on; it may time out once parked

## Container map

| Layer | Path |
|-------|------|
| Logic | `keepawake/KeepAwakeMoving.kt` |
| View | Theme FLAG_KEEP_SCREEN_ON |
| Tests | `src/test/.../keepawake/` |
| Wiring | `shouldKeepScreenAwake` + Theme |

## Tests

- Automated: yes — `KeepAwakeMovingTest`
- Coverage: pref off; parked; unknown; moving

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
