# Compatible Hardware — ExpeditionGauge Core v1

> Validated on OnePlus 12 (USB ADB). Missing hardware is a blocker for ADB rows only — app runs phone-only.

## BLE IMU (Sprint 4)

| Device | Notes |
|--------|-------|
| WitMotion WT901BLECL | Primary v1 target; service UUID FFE0; 50 Hz notify |

## OBD (Sprint 5)

| Device | Transport |
|--------|-----------|
| Generic ELM327 v1.5+ | Bluetooth Classic SPP |

## BLE TPMS (Sprint 5b)

| Device | Parser | Notes |
|--------|--------|-------|
| "BR" valve-stem (SYTPMS-compatible) | `BrTpmsParser` | UUID 0x27A5; [omadon/TPMS_BLE_BR](https://github.com/omadon/TPMS_BLE_BR) |
| PECHAM / SYSGRATION internal | v2 stub | Requires GATT — see tpms-oap |

## External GPS (Sprint 5c)

| Device | Transport |
|--------|-----------|
| Garmin GLO 2 | Bluetooth Classic NMEA SPP |
| Dual XGPS150/160/170 | Bluetooth Classic NMEA SPP |

## Connection budget (OnePlus 12 target)

- 4× IMU GATT
- 1× OBD Classic SPP
- 1× External GPS Classic SPP
- TPMS: scan-only (no GATT)

Document concurrent matrix results in `KNOWLEDGE_BASE.md` after ADB runs.
