# Playback + MapLibre Drift Viz

> Sprint 7 — package `dev.foss.expeditiongauge.playback`

## Acceptance criteria

- ✅ Session list with date, duration, max speed
- ✅ `PlaybackEngine` scrubber drives metrics panel
- ✅ Route polyline β gradient coloring (yellow neutral → cyan left / magenta right)
- ✅ Drift Analysis toggle: body yaw vs velocity heading, slip ratio
- ✅ MapLibre integration path via map placeholder + colored segment tokens

## Design

See [`docs/design/DRIFT_PLAYBACK.md`](../design/DRIFT_PLAYBACK.md).

## Container map

| Layer | Path |
|-------|------|
| Engine | `playback/PlaybackEngine.kt` |
| UI | `ui/playback/PlaybackScreen.kt`, `SessionListScreen.kt` |

## Smoke scenario

1. Open Sessions → select recorded drive
2. Scrub timeline; β readout and route color update in sync
3. Enable Drift Analysis → wedge metrics visible
