# Thermal & Performance Budget

> Sprint 8 — phone-only defaults; external IMU reduces on-phone sensor load.

## Default poll / log rates

| Source | Default rate | Notes |
|--------|--------------|-------|
| Phone IMU fusion | ~50 Hz (SENSOR_DELAY_GAME) | Madgwick on accel+gyro; `SensorPollScheduler.phoneImuSensorDelay` |
| GPS (phone) | 2 Hz (500 ms) | `SensorPollScheduler.PHONE_GPS_INTERVAL_MS` |
| External GPS NMEA | Pass-through (up to 10 Hz) | Capped at RecordingWriter interval |
| OBD PIDs | 5 Hz (200 ms poll) | ELM327 sequential |
| BLE IMU notify | 50 Hz target | WitMotion rate command |
| TPMS BLE scan | On pressure change + idle | Scan-only |
| RecordingWriter | 50 Hz (20 ms) | Tunable in Settings |

## Thermal banner

`ThermalMonitor` triggers dashboard banner:

> Phone warming up — consider external IMU or lower log rate

Shown when device thermal status exceeds normal threshold.

## Mitigations

1. Lower logging rate in Settings (20/10/5 Hz presets)
2. Connect external WitMotion IMU — reduces phone gyro/accel duty
3. Disable optional BLE scan (TPMS) when not recording
4. Sprint 17+: crawl mode de-emphasizes map/GPS rate

## Sprint 3 smoke baseline (ADB)

Automated `thermal-recording` scenario runs a **30 s** recording smoke (start → wait → stop). Full **10 min** phone-only thermal/CPU baseline is manual — log with `adb shell dumpsys thermalservice` during a long session.

## 20-min baseline (ADB Sprint 8)

Manual phone-only session (~20 min drive or desk simulation with motion):

1. Start recording at default 50 Hz log rate
2. Monitor `adb shell dumpsys thermalservice` every 5 min
3. Note whether thermal banner appeared on HUD
4. Record peak CPU if rooted/dev device: `adb shell top -n 1 | head`

Document results in `KNOWLEDGE_BASE.md` (KB-012). Automated smoke uses 30 s `thermal-recording` scenario as a regression gate only.
