# Feature: compose-preferred-framerate

> Vote HIGH `requestedFrameRate` on API 35+ scroll surfaces.

## Acceptance criteria

- ✅ API 35+ votes 120 Hz
- ✅ Older SDKs stay at 60 Hz
- ✅ `highRefreshScroll` uses the helper
- ✅ i18n: silent vote

## Smoke scenario

1. Given a scrolling Settings pane on API 35
2. When the pane composes
3. Then the view requests the high frame-rate category

## Container map

| Layer | Path |
|-------|------|
| Logic | `composepreferredframerate/PreferredFrameRate.kt` |
| Tests | `app/src/test/.../composepreferredframerate/` |
| Wiring | `display/HighRefreshScroll.kt` |

## Tests

- Automated: yes — `PreferredFrameRateTest`
- Coverage: SDK vote

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
