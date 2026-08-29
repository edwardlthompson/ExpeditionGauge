# Feature: playback-speed

> Relive playback speed is variable from 0.25× to 4×.

## Acceptance criteria

- ✅ `PlaybackEngine.setSpeedMultiplier` clamps to 0.25–4
- ✅ UI speed up/down buttons step by 0.25
- ✅ Keyboard `[` / `]` adjust speed
- ✅ i18n: existing `playback_speed_*` strings

## Smoke scenario

1. Given Relive is open
2. When the user taps speed up four times from 1×
3. Then status shows 2.00 and further taps stop at 4×

## Container map

| Layer | Path |
|-------|------|
| Logic | `playbackspeed/PlaybackSpeed.kt` |
| Existing | `playback/PlaybackEngine.kt`, `ui/playback/PlaybackScreenContent.kt` |
| Tests | `app/src/test/.../playbackspeed/` |

## Tests

- Automated: yes — `PlaybackSpeedTest`
- Coverage: clamp min/max; step

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
