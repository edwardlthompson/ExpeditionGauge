# OBD-II Classic + Tire Slip

> Sprint 5 — package `dev.foss.expeditiongauge.obd`, `dev.foss.expeditiongauge.slip`

## Acceptance criteria

- ELM327 over Bluetooth Classic SPP (`ObdClassicManager` + `Elm327Protocol` init: ATZ, ATE0, ATL0, ATSP0)
- Poll RPM (010C), speed (010D), throttle (0111), load (0104), voltage (0142); optional rear wheels (015A/B)
- PID enable toggles in Settings (`ObdPidConfig`)
- OBD speed overlays speedometer (`speedFromObd` + "OBD speed" label)
- `slipRatio` / `rearSlipRatio` on TelemetryBus — distinct from `driftAngleDeg` (β)
- `slipSource` + rear slip in sample `extrasJson`
- Shares `ClassicBluetoothBudget` with external GPS (max 2 SPP)

## Container map

| Layer | Path |
|-------|------|
| OBD | `obd/ObdClassicManager.kt`, `obd/Elm327Protocol.kt`, `obd/ClassicBluetoothBudget.kt` |
| Slip | `slip/TireSlipCalculator.kt` |
| UI | Settings OBD picker + PID toggles; dashboard slip/rear slip + RPM |
| ADB log tag | `ExpeditionGauge/Obd` |

## Terminology

| Field | Meaning |
|-------|---------|
| `driftAngleDeg` (β) | Vehicle sideslip — body yaw vs GPS velocity heading (degrees) |
| `slipRatio` | Tire longitudinal slip `(wheelSpeed − gpsSpeed) / gpsSpeed` |

## ADB smoke scenarios

| Scenario | Command | Notes |
|----------|---------|-------|
| ELM327 PIDs | `adb-smoke.ps1 -Sprint 5 -Scenario obd-elm327` | Requires paired adapter; exit 2 if absent |
| β vs slip | `adb-smoke.ps1 -Sprint 5 -Scenario obd-slip-beta` | Phone-only: verifies β in `ImuFusion` log; full slip needs OBD |

Manual: Settings → select OBD device → verify RPM/throttle in logcat and slip ratio above ~5 km/h when wheel speed differs from GPS.
