# Feature: playback-gamepad

> Gamepad and extra keys scrub Relive the same way as the keyboard.

## Acceptance criteria

- ✅ A / Start toggles play
- ✅ L1 / R1 seek 1 s
- ✅ Minus / Plus change speed
- ✅ Existing arrow / media keys still work

## Smoke scenario

1. Given Relive is focused
2. When a gamepad A button is pressed
3. Then playback toggles play/pause

## Container map

| Layer | Path |
|-------|------|
| Logic | `playbackgamepad/PlaybackGamepadMap.kt` |
| Tests | `app/src/test/.../playbackgamepad/` |
| Wiring | `PlaybackInputHandler` |

## Tests

- Automated: yes — `PlaybackGamepadMapTest`
- Coverage: face/shoulder/speed keys

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
