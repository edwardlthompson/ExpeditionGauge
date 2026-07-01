# Elevation profile

> Sprint 23 — smoothed altitude chart synced to playback scrubber.

## Overview

Shows a filled elevation profile for the active session with min/max altitude and total ascent/descent stats. Tap the chart to seek; the red cursor tracks `PlaybackEngine.currentIndex` during scrub and play.

## Architecture

| Layer | Path |
|-------|------|
| Builder | `playback/ElevationProfileBuilder.kt` — gap fill, 5-sample smoothing, stats |
| Panel | `ui/playback/ElevationProfilePanel.kt` — canvas + tap-to-seek |
| Coordinator | `playback/PlaybackEngine.kt` |

## Acceptance criteria

- Smoothed elevation line when ≥2 altitude samples exist
- Stats row: min, max, total ascent, total descent (meters)
- Scrubber / tap on chart seeks playback index
- Red cursor follows current sample index
- Hidden when `FeatureFlags.elevationProfileEnabled` is false

## Feature flag

`project.config.json` → `sprints.v2_elevation_profile` → `FeatureFlags.elevationProfileEnabled`

## ADB

`elevation-playback-scrub` — open playback, verify elevation stats visible, scrub and confirm UI stable.

## Definition of Done

- `check-v2-elevation-gate.sh` green
- Unit tests in `ElevationProfileBuilderTest.kt`
