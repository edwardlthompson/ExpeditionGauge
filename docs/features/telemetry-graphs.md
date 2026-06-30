# Feature: Telemetry Graphs

> Sprint 11 — playback time-series graphs synced to scrubber.

## Acceptance criteria

- ✅ Scrollable graph dock: speed, attitude (pitch/roll/latG), tire data tabs
- ✅ Graph cursor linked to PlaybackEngine.currentIndex
- ✅ Tap graph to seek scrubber
- ✅ Decimation for sessions > 10k points
- ✅ Missing OBD/TPMS channels hidden

## Smoke scenario

1. Given a loaded playback session
2. When user scrubs timeline
3. Then graph cursor moves and map/gauge position matches

## Container map

| Layer | Path |
|-------|------|
| View | `ui/playback/TelemetryGraphPanel.kt` |
| Renderer | `TelemetryGraphRenderer` (in same file) |
| Coordinator | `playback/PlaybackEngine.kt` |

## Definition of Done

- FeatureFlags.telemetryGraphsEnabled gates graph panel
- Unit tests for PlaybackEngine marker/scrub logic
