# Feature: gpx-beta-extensions

> GPX track points include namespaced latG, lonG, and β.

## Acceptance criteria

- ✅ Extensions use `xmlns:eg` ExpeditionGauge schema
- ✅ lonG is exported alongside latG and β
- ✅ Missing values omit the tag
- ✅ i18n: none (export XML)

## Smoke scenario

1. Given a session with latG and drift angle
2. When exported as GPX
3. Then each point includes `eg:latG` / `eg:betaDeg` under extensions

## Container map

| Layer | Path |
|-------|------|
| Logic | `gpxbeta/GpxBetaExtensions.kt` |
| Tests | `app/src/test/.../gpxbeta/` |
| Wiring | `ExportFormatters.toGpx` |

## Tests

- Automated: yes — `GpxBetaExtensionsTest`
- Coverage: namespaced tags and xmlns

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
