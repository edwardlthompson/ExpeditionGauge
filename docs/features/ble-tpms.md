# BLE TPMS (valve-stem)

> Sprint 5b — package `dev.foss.expeditiongauge.ble.tpms`

## Acceptance criteria

- ✅ Advertisement-first scan (no GATT) for "BR" / UUID 0x27A5 sensors
- ✅ `BrTpmsParser` decodes omadon fixture; absolute→relative pressure
- ✅ `TpmsSnapshot` on TelemetryBus; live `TirePressurePanel` data
- ✅ Gated by `FeatureFlags.tpmsEnabled` (default off)

- ✅ Settings: enable toggle, TPMS management screen, per-corner assign, PSI/kPa + °C/°F
- ✅ Auto-scan when recording starts (if TPMS enabled)
- ✅ `slipTpmsCorrelation` block in `extrasJson` when slip + TPMS present

## References

- [omadon/TPMS_BLE_BR](https://github.com/omadon/TPMS_BLE_BR) — primary v1 protocol
- [KreAch3R/tpms-oap](https://github.com/KreAch3R/tpms-oap) — v2 parser families

## Container map

| Layer | Path |
|-------|------|
| Parser | `ble/tpms/TpmsParser.kt`, `BrTpmsParser.kt` |
| Manager | `ble/tpms/BleTpmsManager.kt`, `TpmsDeviceSession.kt`, `TpmsTelemetryLog.kt` |
| UI | `ui/settings/TpmsManagementScreen.kt`, `ui/components/gauge/TirePressurePanel.kt` |
| Fixture | `ble/tpms/fixtures/br_ad_example.hex` |

## Smoke scenario

1. Enable TPMS in build flags / settings
2. Install 4 "BR" sensors; Settings → assign corners
3. Tire panel shows live PSI; stale corners gray after 60 s
