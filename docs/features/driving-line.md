# Feature: Driving Line Analysis

> Sprint 12 — apex markers, brake zones, latG offset bands.

## Acceptance criteria

- ✅ Apex points from local latG peaks per lap segment
- ✅ Brake zones where lonAccel < threshold
- ✅ Offset segments for entry/exit emphasis
- ✅ Playback toolbar toggle: Route | DrivingLine | Both

## Container map

| Layer | Path |
|-------|------|
| Logic | `drivingline/DrivingLineAnalyzer.kt`, `DrivingLineGeoJsonBuilder.kt` |
| Map | `ui/playback/PlaybackMapView.kt` |
| Controls | `ui/playback/PlaybackOverlayControls.kt` |

## Definition of Done

- FeatureFlags.drivingLineEnabled gates overlay mode
- Uses logged GPS + fusion data only (phone-only OK)
