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
- Sprint 28: pair via system Bluetooth → select → ELM handshake validate; cold-start auto-reconnect; Forget OBD; connection status in Settings
- After each successful OBD connect: **one-shot Mode 03** stored-DTC read (no Mode 04 clear; no continuous spam). Codes resolve via vendored **OBDex CC0** English titles (`assets/dtc/obdex_en.json`) — the same catalog [OBDForge](https://github.com/edwardlthompson/OBDForge) uses; **no GPL OBDForge code**. Regen: `pwsh scripts/expedition/fetch-obdex-dtc.ps1`
- Android Auto **ROW** (3×1) Drive HUD: bold-red single-line footer carousel (`n/N` + code + truncated title, 5 s dwell). **COLUMN** (1×2) omits the footer. Phone Compose HUD: out of scope this slice

## Container map

| Layer | Path |
|-------|------|
| OBD | `obd/ObdClassicManager.kt`, `obd/Elm327Protocol.kt`, `obd/ClassicBluetoothBudget.kt` |
| DTC | `obd/dtc/DtcCatalog.kt`, `DtcCarousel.kt`, `assets/dtc/obdex_en.json` |
| Slip | `slip/TireSlipCalculator.kt` |
| UI | Settings OBD picker + PID toggles; dashboard slip/rear slip + RPM; AA ROW DTC footer |
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
