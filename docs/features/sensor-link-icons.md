# Feature: Sensor link icons (Sprint 28)

> Telemetry cube bottom row — GPS / OBD / TPMS / IMU illuminated when linked.

## Acceptance criteria

- Bottom row of phone telemetry cube shows four link icons
- Illuminated (full tint) when linked; grayed when disconnected
- GPS uses `gpsFix`; OBD uses `obdConnected`; TPMS any non-stale assigned pressure; IMU any connected session
- AA telemetry cube shows G/O/T/I letter badges with the same semantics
- Mapping via pure `SensorLinkState.from(snapshot)` (unit tested)

## Container map

| Layer | Path |
|-------|------|
| Logic | `telemetry/SensorLinkState.kt` |
| Phone UI | `ui/dashboard/hud/TelemetryHudLinkRow.kt` |
| AA | `car/gauge/DriveHudCubeDraw.kt` link row |
| Tests | `telemetry/SensorLinkStateTest.kt` |
