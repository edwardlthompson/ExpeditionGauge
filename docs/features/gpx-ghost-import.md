# Feature: gpx-ghost-import

> Import GPX track points or FIT lat,lon[,ts] lines as ghost samples.

## Acceptance criteria

- ✅ GPX `trkpt lat lon` parses
- ✅ CSV/FIT `lat,lon[,timestampMs]` parses when GPX is absent
- ✅ Samples remap onto a ghost session id
- ✅ i18n: none (import)

## Smoke scenario

1. Given a GPX file with one track point
2. When it is imported as ghost
3. Then PlaybackEngine.loadGhost receives one sample

## Container map

| Layer | Path |
|-------|------|
| Logic | `gpxghostimport/GpxGhostImport.kt` |
| Tests | `app/src/test/.../gpxghostimport/` |
| Wiring | `PlaybackEngine.loadGhost` |

## Tests

- Automated: yes — `GpxGhostImportTest`
- Coverage: GPX and FIT lines

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
