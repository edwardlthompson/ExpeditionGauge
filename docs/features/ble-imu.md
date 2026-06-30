# BLE External IMU (WitMotion WT901BLECL)

> Sprint 4 — package `dev.foss.expeditiongauge.ble`

## Acceptance criteria

- Scan/connect up to 4 WitMotion BLE IMUs via GATT
- Per-device placement label FL/FR/RL/RR in session
- Dashboard IMU status strip with green/yellow/red signal quality
- Single IMU improves body yaw for drift β; multi-IMU weighted fusion via `MultiImuYawFusion`
- Phone-only fallback when all IMUs disconnect
- Per-IMU raw/filtered yaw in recording `extrasJson` (`imuDevices` array)
- Auto-reconnect on unexpected GATT disconnect (user disconnect excluded)

## Container map

| Layer | Path |
|-------|------|
| Parser | `ble/WitMotionParser.kt` (`WitMotionPacketParser` alias) |
| Scan / budget | `ble/BleScanCoordinator.kt`, `ble/BleConnectionBudget.kt`, `obd/ClassicBluetoothBudget.kt` |
| Manager | `ble/BleImuManager.kt`, `ble/ImuDeviceSession.kt`, `ble/BleImuGattCallback.kt` |
| Fusion | `fusion/ImuOrientationFilter.kt`, `fusion/MultiImuYawFusion.kt` |
| UI | `ui/settings/ImuManagementScreen.kt`, `ui/components/gauge/ImuStatusStrip.kt` |
| ADB log tag | `ExpeditionGauge/ImuFusion` |

## Protocol

- Service UUID `0000ffe0-0000-1000-8000-00805f9b34fb`
- Notify `0000ffe4-…`, Write `0000ffe9-…`
- Packet 0x61: accelerometer + gyro + Euler angles
- Rate command: `FF AA 03 08 00` (50 Hz)

## ADB smoke scenarios

| Scenario | Command | Notes |
|----------|---------|-------|
| Phone fallback | `adb-smoke.ps1 -Sprint 4 -Scenario imu-fallback` | No IMU connected; logcat `fusionSource=phone` |
| Single WT901 | `adb-smoke.ps1 -Sprint 4 -Scenario imu-single` | Requires WT901BLECL; exit 2 if absent |
| Multi 2–4 | `adb-smoke.ps1 -Sprint 4 -Scenario imu-multi` | Requires ≥2 WitMotion devices; exit 2 if absent |

Manual: Settings → IMU Devices → Scan → Connect → assign FL/RL → verify dashboard strip + `ExpeditionGauge/ImuFusion` logcat at ≥20 Hz.
