# BLE External IMU (WitMotion WT901BLECL)

> Sprint 4 — package `dev.foss.expeditiongauge.ble`

## Acceptance criteria

- ✅ Scan/connect up to 4 WitMotion BLE IMUs via GATT
- ✅ Per-device placement label FL/FR/RL/RR persisted in session
- ✅ Dashboard IMU status strip with signal quality
- ✅ Single IMU improves body yaw for drift β; multi-IMU weighted fusion via `MultiImuYawFusion`
- ✅ Phone-only fallback when all IMUs disconnect

## Container map

| Layer | Path |
|-------|------|
| Parser | `ble/WitMotionParser.kt` |
| Manager | `ble/BleImuManager.kt`, `ble/ImuDeviceSession.kt` |
| Fusion | `fusion/ImuOrientationFilter.kt`, `fusion/MultiImuYawFusion.kt` |
| UI | `ui/settings/ImuManagementScreen.kt`, `ui/components/gauge/ImuStatusStrip.kt` |

## Protocol

- Service UUID `0000ffe0-0000-1000-8000-00805f9b34fb`
- Notify `0000ffe4-…`, Write `0000ffe9-…`
- Packet 0x61: accelerometer + gyro + Euler angles
- Rate command: `FF AA 03 XX 00` (50–100 Hz when recording)

## Smoke scenario

1. Pair one WT901BLECL in Android Bluetooth settings
2. Settings → IMU Devices → Scan → Connect → assign FL
3. Dashboard strip shows green FL indicator; attitude/β updates at ≥20 Hz
