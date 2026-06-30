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

1. `SensorFusionEngine` provides body yaw.
2. GPS velocity heading updates β when speed ≥ 2 m/s.
3. Below threshold, β updates suppressed (unreliable at crawl speed).

## UI

- Compact β readout in `GpsReadoutPanel` when speed > 2 m/s.
- Settings toggle for drift display (future sprint).

## Tests

- `SideslipEkfTest` — known yaw + velocity heading → expected β
- `DriftAngleEstimatorTest` — low-speed β suppressed

See ADR: [`docs/adr/0003-sensor-architecture.md`](../adr/0003-sensor-architecture.md)
