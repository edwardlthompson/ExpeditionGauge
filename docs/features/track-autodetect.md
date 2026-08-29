# Feature: track-autodetect

> Infer a start/finish gate when a GPS trail closes a loop.

## Acceptance criteria

- ✅ Loop must travel at least 200 m before closing
- ✅ Close if the trail returns within 25 m of the first fix
- ✅ Writes TrackLineBuilder start/finish GeoJSON
- ✅ Track setup can run detect from the latest recorded session
- ✅ i18n: `track_setup_autodetect*`

## Smoke scenario

1. Given a recorded session that returns to its start
2. When Track setup taps Auto-detect
3. Then a start/finish line is saved

## Container map

| Layer | Path |
|-------|------|
| Logic | `trackautodetect/TrackAutodetect.kt` |
| Tests | `app/src/test/.../trackautodetect/` |
| Wiring | `TrackSetupScreen`, `AppScreenRouter` |

## Tests

- Automated: yes — `TrackAutodetectTest`
- Coverage: closed loop; short/open reject

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
