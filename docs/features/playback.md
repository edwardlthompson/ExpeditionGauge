# Playback + MapLibre Drift Viz

> Sprint 7 — package `dev.foss.expeditiongauge.playback`

## Acceptance criteria

- ✅ Session list with date, duration, max speed (`SessionListScreen`)
- ✅ `PlaybackEngine` scrubber drives metrics panel, map camera, elevation cursor
- ✅ MapLibre route polyline: β gradient (yellow neutral → cyan left / magenta right) + lonAccel brake/accel buckets
- ✅ LatG width bands via `widthBucket`; slip overlay layer (`route-slip`)
- ✅ Vehicle drift overlay on map (heading vs velocity wedge; tail ∝ |β|)
- ✅ Drift Analysis toggle: `DriftAnalysisCanvas` with vehicle outline, vectors, multi-IMU corners
- ✅ Elevation profile; camera `animateTo()` follow; metrics panel (β, latG, slip, RPM, throttle, TPMS)

## Design

See [`docs/design/DRIFT_PLAYBACK.md`](../design/DRIFT_PLAYBACK.md).

## Container map

| Layer | Path |
|-------|------|
| Engine | `playback/PlaybackEngine.kt`, `PlaybackModels.kt` |
| Route | `playback/DriftRouteStyling.kt`, `RouteGeoJsonBuilder.kt`, `SampleImuExtras.kt` |
| Map | `ui/playback/PlaybackMapView.kt` |
| UI | `ui/playback/PlaybackScreen.kt`, `VehicleDriftOverlay.kt`, `DriftAnalysisCanvas.kt`, `ElevationProfile.kt` |

## Smoke scenario

1. Sessions → select recorded drive
2. Scrub timeline; β readout and map camera update in sync
3. Enable Drift Analysis → wedge + metrics visible; no GL crash
