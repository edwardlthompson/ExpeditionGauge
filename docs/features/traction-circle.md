# Feature: traction-circle

> Live latG/lonG friction circle on the G-force HUD, with a trail while driving.

## Acceptance criteria

- ✅ G-force mode maps the ball from latG/lonG (not pitch/roll)
- ✅ Points outside 1.5 G are scaled onto the circle
- ✅ Trail draws live in G-force mode, not only while recording
- ✅ i18n: none

## Smoke scenario

1. Given the attitude tile is G-force
2. When the vehicle corners
3. Then the ball tracks lateral/longitudinal G inside the circle and leaves a short trail

## Container map

| Layer | Path |
|-------|------|
| Logic | `tractioncircle/TractionCircle.kt` |
| View | `AttitudeGMeterGauge` G-force path |
| Tests | `src/test/.../tractioncircle/` |
| Wiring | `ballForMode` + live trail |

## Tests

- Automated: yes — `TractionCircleTest`
- Coverage: clamp; live-trail flag

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
