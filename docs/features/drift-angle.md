# Drift Angle (β) — Sprint 3

## Terminology

| Term | Meaning |
|------|---------|
| **β (drift angle / sideslip)** | Signed angle between vehicle body heading and velocity heading |
| **Tire slip ratio** | Wheel-speed vs ground-speed (OBD, Sprint 5) — separate from β |

Formula:

```
driftAngleDeg = normalize(bodyYawDeg − velocityHeadingDeg)
```

## Components

| Class | Package | Role |
|-------|---------|------|
| `SideslipEkf` | `drift/` | Lightweight state `[yaw, yawRate, β]` |
| `DriftAngleEstimator` | `drift/` | Fuses body yaw + GPS velocity heading |
| `PhoneGpsProvider` | `gps/` | Speed + bearing from `LocationManager` |

## Phone-only path

1. `SensorFusionEngine` provides body yaw (`bodyYawDeg`) for attitude and β.
2. HUD `headingDeg` is GNSS **chip course-over-ground** (`Location.bearing` / NMEA RMC)
   while moving. Lat/lon deltas are a fallback only after ≥8 m of GPS-only travel.
   A missing bearing is **not** written as 0° (due north); last good COG is held.
3. Phone IMU/mag yaw is used for HDG only when no GPS course has been established
   (vehicle steel distorts mag; Madgwick yaw initializes at 0°).
4. Below 2 m/s, β updates suppressed (unreliable at crawl speed). GPS HDG still holds.

## UI

- Compact β readout in `GpsReadoutPanel` when speed > 2 m/s.
- Settings toggle for drift display (future sprint).

## Tests

- `SideslipEkfTest` — known yaw + velocity heading → expected β
- `DriftAngleEstimatorTest` — low-speed β suppressed

See ADR: [`docs/adr/0003-sensor-architecture.md`](../adr/0003-sensor-architecture.md)
