# Feature: Ghost Lap Comparison

> Sprint 12 — compare two laps with delta overlay.

## Acceptance criteria

- ✅ Load ghost lap sample stream alongside primary
- ✅ Semi-transparent route overlay (map layer stub)
- ✅ Delta time at scrubber position
- ✅ Warn and disable when startLine mismatch > 50 m
- ✅ Side-by-side sector metrics table (playback UI stub)

## Container map

| Layer | Path |
|-------|------|
| Logic | `ghost/GhostLapOverlay.kt` |
| Coordinator | `playback/PlaybackEngine.loadGhost()` |
| Tests | `ghost/GhostLapOverlayTest.kt` |

## Definition of Done

- FeatureFlags.ghostLapEnabled gates compare UI
- Cross-session compare validates start/finish GeoJSON proximity
