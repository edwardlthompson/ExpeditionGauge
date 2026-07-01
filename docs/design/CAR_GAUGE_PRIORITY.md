# Car gauge priority (maintainer default)

Approved default order for Android Auto telemetry rows (Sprint 21).

| Rank | Metric key | Label | Source field |
|------|------------|-------|--------------|
| 1 | `speed` | Speed | `TelemetrySnapshot.speedMps` |
| 2 | `latG` | Lat G | `TelemetrySnapshot.latG` |
| 3 | `pitch` | Pitch | `TelemetrySnapshot.pitchDeg` |
| 4 | `roll` | Roll | `TelemetrySnapshot.rollDeg` |
| 5 | `beta` | Drift β | `TelemetrySnapshot.driftAngleDeg` |
| 6 | `rpm` | RPM | `TelemetrySnapshot.rpm` |
| 7 | `throttle` | Throttle | `TelemetrySnapshot.throttlePct` |

Settings allowlist defaults to the first six keys. Throttle is optional when OBD connected.
