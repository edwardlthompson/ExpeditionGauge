# Drift Playback Visualization

> Sprint 7 design reference

## Route coloring

| β range | Color token | Meaning |
|---------|-------------|---------|
| \|β\| ≤ 5° | `DriftNeutral` (yellow) | Straight / neutral |
| β > 5° | `DriftLeft` (cyan) | Left drift |
| β < −5° | `DriftRight` (magenta) | Right drift |
| lonAccel < −0.4g | `LonAccelBrake` (red) | Hard braking |
| lonAccel > +0.35g | `LonAccelAccel` (green) | Strong accel |

Segment `colorBucket` precomputed in `RouteGeoJsonBuilder`; MapLibre `LineLayer` uses data-driven `switch` on `colorBucket`.

## Line width (latG bands)

| \|latG\| | `widthBucket` | Width |
|----------|---------------|-------|
| < 0.3g | 0 | 3 dp |
| 0.3–0.7g | 1 | 5 dp |
| 0.7–1.2g | 2 | 7 dp |
| ≥ 1.2g | 3 | 10 dp |

## Slip overlay

Separate GeoJSON source; orange `route-slip` layer with opacity from `slipAlpha` (distinct from β coloring).

## Vehicle overlay

- Icon heading = `bodyYawDeg` (yellow)
- Velocity vector = `velocityHeadingDeg` (cyan)
- Wedge arc magnitude = `driftAngleDeg` (tail ∝ |β|)
- Rendered in `VehicleDriftOverlay` atop MapLibre map (map camera keeps vehicle near center)

## Sync contract

`PlaybackEngine.state` is the single scrubber clock consumed by:

- Map polyline + vehicle marker (`PlaybackMapView`)
- Gauge numeric readouts (`PlaybackMetricsPanel`)
- Drift analysis panel (`DriftAnalysisCanvas`)
- Elevation profile cursor
- (Sprint 11) time-series graphs (`TelemetryGraphPanel`)

## Multi-IMU playback

When `extrasJson.imuDevices` present, per-corner latG vectors shown in `DriftAnalysisCanvas` (`SampleImuExtras`).
