# BLE TPMS (valve-stem)

> Sprint 5b — package `dev.foss.expeditiongauge.ble.tpms`
> Sprint 29 — QR pairing wizard (`ui/settings/tpms/`)

## Acceptance criteria

- ✅ Advertisement-first scan (no GATT) for "BR" / UUID 0x27A5 sensors
- ✅ `BrTpmsParser` decodes omadon fixture; absolute→relative pressure
- ✅ `TpmsSnapshot` on TelemetryBus; live `TirePressurePanel` data
- ✅ Gated by `FeatureFlags.tpmsEnabled` (default off)

- ✅ Settings: enable toggle, TPMS management screen, per-corner assign, PSI/kPa + °C/°F
- ✅ Auto-scan when recording starts (if TPMS enabled)
- ✅ `slipTpmsCorrelation` block in `extrasJson` when slip + TPMS present
- ✅ Sprint 28: corner assignments persisted across launches; Manage screen “link sensors” copy
- ✅ Sprint 29: guided QR wizard (CameraX + ZXing FOSS decode); exclusive MAC↔corner; ghost sessions for remembered-but-unseen sensors; manual MAC fallback when camera denied or QR is not a MAC
- ✅ Moman C4 / DJTPMS / BR short binding IDs (4/6/8 hex MAC suffix) resolve via **live ads only** (no OUI guessing)

## References

- [omadon/TPMS_BLE_BR](https://github.com/omadon/TPMS_BLE_BR) — primary v1 protocol (BR / 0x27A5)
- [Zen3515/djtpms](https://github.com/Zen3515/djtpms) — DJTPMS (Moman app ecosystem) payload notes
- [andi38/TPMS](https://github.com/andi38/TPMS) — BLE ad layout
- [KreAch3R/tpms-oap](https://github.com/KreAch3R/tpms-oap) — v2 parser families

## Moman C4 / short QR IDs

Official Moman apps use **DJTPMS**. Sensors are advertisement-only BR modules (name `BR`, service `0x27A5`) — not Classic-paired. Printed QR codes encode a **short binding ID** = last 3 bytes of the BLE MAC (community: eucplanet / omadon). The wizard matches that suffix against **live advertisements only**. Guessing `AC:15:85…` vs `3B:60:00…` was removed after it produced wrong bindings.

## Container map

| Layer | Path |
|-------|------|
| Parser | `ble/tpms/TpmsParser.kt`, `BrTpmsParser.kt`, `TpmsQrPayloadParser.kt` |
| Manager | `ble/tpms/BleTpmsManager.kt`, `TpmsCornerAssignLogic.kt`, `TpmsDeviceSession.kt` |
| Wizard UI | `ui/settings/tpms/*` |
| Manage UI | `ui/settings/TpmsManagementScreen.kt`, `ui/components/gauge/TirePressurePanel.kt` |
| Fixture | `ble/tpms/fixtures/br_ad_example.hex` |
## Smoke scenario

1. Enable TPMS in Settings
2. **Wizard:** Settings → TPMS Sensors → Setup wizard → scan (or enter) MAC for FL→FR→RL→RR → Confirm each → Summary
3. Kill app; reopen Manage — four ghost rows with MACs (“Waiting for signal”) before any ad
4. Power sensors; ads fill PSI on HUD; ghosts become Active
5. **Deny camera:** wizard still completes via manual MAC entry
6. **Duplicate MAC:** assign same MAC to FR after FL — only FR remains assigned
7. Paste BLE MAC from sensor label if the printed QR is a serial (not a MAC) — parser fails closed
