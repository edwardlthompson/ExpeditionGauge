# Feature: Ghost Lap Comparison

> Sprint 12 — compare two laps with delta overlay.

## Acceptance criteria

- ✅ Load ghost lap sample stream alongside primary
- ✅ Semi-transparent route overlay on map (`RouteGeoJsonBuilder.buildGhostRouteGeoJson`)
- ✅ Distance-aligned delta time at scrubber
- ✅ Warn and disable when start/finish mismatch > 50 m
- ✅ Side-by-side sector metrics table (`GhostLapComparePanel`)

## Container map

| Layer | Path |
|-------|------|
| Logic | `ghost/GhostLapOverlay.kt`, `GhostLapComparer.kt` |
| UI | `ui/playback/GhostLapComparePanel.kt` |
| Loader | `playback/PlaybackSessionLoader.loadWithGhost()` |

## Definition of Done

- FeatureFlags.ghostLapEnabled gates compare UI
- Cross-session compare validates start/finish GeoJSON proximity
