# Sensor Fusion — Sprint 3

## Overview

Phone-only sensor fusion produces pitch, roll, yaw, and lateral/longitudinal G for the HUD and `TelemetryBus`.

## Components

| Class | Package | Role |
|-------|---------|------|
| `PhoneSensorProvider` | `sensors/` | Android `SensorManager` accelerometer + gyroscope |
| `MadgwickFilter` | `fusion/` | AHRS orientation (primary) |
| `ComplementaryFilter` | `fusion/` | Fallback orientation filter |
| `SensorFusionEngine` | `fusion/` | Applies calibration offsets; outputs `FusionOutput` |
| `CalibrationStore` | `calibration/` | Pitch/roll zero from Set Level |

## Data flow

```
PhoneSensorProvider → SensorFusionEngine → TelemetryBus → DashboardViewModel
CalibrationStore    ↗
```

## Rates

- Target 50 Hz during active HUD (`SENSOR_DELAY_GAME`).
- Thermal throttling may reduce effective rate; see `ThermalMonitor`.

## Tests

- `MadgwickFilterTest` — quaternion normalization, bounded pitch/roll
- `SensorFusionEngineTest` — calibration offset application
- `ComplementaryFilterTest` — integration with synthetic gyro/accel

## FOSS constraint

No Google Play Services fused sensors. Raw `SensorManager` APIs only.

See ADR: [`docs/adr/0003-sensor-architecture.md`](../adr/0003-sensor-architecture.md)
