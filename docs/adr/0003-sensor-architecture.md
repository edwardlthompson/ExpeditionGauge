# ADR-0003: Sensor Architecture

- **Status:** Accepted
- **Date:** 2026-06-29
- **Deciders:** ExpeditionGauge team

## Context

ExpeditionGauge must deliver accurate attitude, G-force, heading, and drift angle (β) on phone-only hardware first, then layer external BLE IMU, TPMS, OBD, and external GPS without rewriting the HUD or recording pipeline.

## Decision

Adopt a **Hexagonal (Ports & Adapters)** sensor stack:

| Layer | Package | Responsibility |
|-------|---------|----------------|
| **Telemetry port** | `telemetry/TelemetryBus` | Single `Flow<TelemetrySnapshot>` for UI, recording, alerts, live stream |
| **Phone adapters** | `sensors/PhoneSensorProvider`, `gps/PhoneGpsProvider` | Android `SensorManager` + `LocationManager` (no Play Services) |
| **Fusion domain** | `fusion/SensorFusionEngine` | `SensorAxisRemap` → Madgwick/complementary → vehicle pitch/roll (ADR-0013) |
| **Drift domain** | `drift/DriftAngleEstimator` | Lightweight EKF sideslip β from body yaw + GPS velocity heading |
| **Calibration** | `calibration/CalibrationStore` | DataStore offsets for pitch/roll zero |
| **Thermal guard** | `thermal/ThermalMonitor` | Non-blocking banner when device throttling detected |

### Data flow (phone-only v1)

```
PhoneSensorProvider ──► SensorFusionEngine ──► DriftAngleEstimator
PhoneGpsProvider    ─────────────────────────►       │
CalibrationStore    ──► SensorFusionEngine           │
                                                     ▼
                                              TelemetryBus
                                                     │
                                              DashboardViewModel
```

### Filter stack

1. **Orientation:** Remap accel/gyro to a screen-stable frame (`SensorAxisRemap` by
   `displayRotation`) **before** Madgwick AHRS; complementary filter fallback;
   then locked portrait pitch↔roll swap (`VehicleAttitudeLogic`). See ADR-0013 —
   do not fix landscape with post-fusion Euler unwrap.
2. **Sideslip:** `SideslipEkf` state `[yaw, yawRate, β]`; GPS velocity heading updates suppressed below 2 m/s.
3. **Rate:** 50 Hz fusion target during active HUD; providers may downsample under thermal pressure.

### Extension points (Sprint 4+)

- `BleImuManager` publishes per-corner IMU samples into `SensorFusionEngine` (preferred yaw when calibrated).
- `FusedGpsLocationProvider` replaces direct `PhoneGpsProvider` feed (Sprint 5c).
- `BleTpmsManager` and OBD publish directly to `TelemetryBus` extras, not through fusion.

## Consequences

- UI reads only `TelemetryBus`; no direct `SensorManager` calls in composables.
- Phone-only path is always functional; external hardware is additive.
- Unit tests target pure filter logic (`MadgwickFilter`, `AttitudeBallLogic`, `SideslipEkf`) without Robolectric where possible.
- FOSS only: no Firebase, Play Services fused location, or proprietary sensor SDKs.

## Alternatives Considered

| Alternative | Rejected because |
|-------------|------------------|
| ViewModels subscribe to sensors directly | Breaks recording/alerts/live reuse of same stream |
| Play Services FusedLocationProvider | Violates FOSS / F-Droid policy |
| Full 15-state EKF on phone | CPU/thermal cost too high for v1; lightweight β EKF sufficient |
