# OBD-II Classic + Tire Slip

> Sprint 5 — package `dev.foss.expeditiongauge.obd`, `dev.foss.expeditiongauge.slip`

## Acceptance criteria

- ✅ ELM327 over Bluetooth Classic SPP (`ObdClassicManager`)
- ✅ Poll RPM, speed, throttle, load, battery voltage
- ✅ `slipRatio` on TelemetryBus — distinct from `driftAngleDeg` (β)
- ✅ Settings OBD device picker
- ✅ Shares `ClassicBluetoothBudget` with external GPS (max 2 SPP)

## Container map

| Layer | Path |
|-------|------|
| OBD | `obd/ObdClassicManager.kt`, `obd/ClassicBluetoothBudget.kt` |
| Slip | `slip/TireSlipCalculator.kt` |
| UI | Settings OBD picker, dashboard slip readout |

## Smoke scenario

1. Pair ELM327 adapter
2. Settings → select OBD device → connect
3. Dashboard shows RPM/voltage; slip ratio appears above ~5 km/h when wheel speed differs from GPS
