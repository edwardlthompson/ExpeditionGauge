# Thermal & Performance Budget

> Sprint 8 — phone-only defaults; external IMU reduces on-phone sensor load.

## Default poll / log rates

| Source | Default rate | Notes |
|--------|--------------|-------|
| Phone IMU fusion | ~50 Hz (SENSOR_DELAY_GAME) | Madgwick on accel+gyro |
| GPS (phone) | 2 Hz (500 ms) | LocationManager |
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

## 20-min baseline (ADB Sprint 8)

Record CPU/thermal on phone-only 20-min session; document in `KNOWLEDGE_BASE.md`.
