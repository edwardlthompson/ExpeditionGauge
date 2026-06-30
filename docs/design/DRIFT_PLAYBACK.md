# Drift Playback Visualization

> Sprint 7 design reference

## Route coloring

| β range | Color token | Meaning |
|---------|-------------|---------|
| \|β\| ≤ 5° | `driftNeutral` (yellow) | Straight / neutral |
| β > 5° | `driftLeft` (cyan) | Left drift |
| β < −5° | `driftRight` (magenta) | Right drift |

Segment color derived from `SampleEntity.driftAngleDeg` at each GPS point.

## Vehicle overlay

- Icon heading = `bodyYawDeg`
- Velocity vector tangent to GPS path
- Wedge arc magnitude = `abs(driftAngleDeg)` (capped for readability)
- Tail length ∝ \|β\| when Drift Analysis enabled

## Sync contract

`PlaybackEngine.state` is the single scrubber clock consumed by:

- Map polyline highlight index
- Gauge numeric readouts
- Drift analysis panel
- (Sprint 11) time-series graphs

## Multi-IMU playback

When `extrasJson.imu` present, per-corner force vectors shown in Drift Analysis toggle (Sprint 7 basics; full vectors in Sprint 11).
