# Feature: video-burnin-fields

> Choose which telemetry fields burn into exported video.

## Acceptance criteria

- ✅ Field ids: speed, beta, latG, lonG, pitch, roll
- ✅ Empty selection falls back to all
- ✅ Overlay compositor uses the picker
- ✅ i18n: none (field ids)

## Smoke scenario

1. Given only speed and latG are enabled
2. When burn-in export runs
3. Then overlay lines are those two fields

## Container map

| Layer | Path |
|-------|------|
| Logic | `videoburninfields/VideoBurnInFields.kt` |
| Tests | `app/src/test/.../videoburninfields/` |
| Wiring | `VideoOverlayCompositor` |

## Tests

- Automated: yes — `VideoBurnInFieldsTest`
- Coverage: pick order; encode

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
