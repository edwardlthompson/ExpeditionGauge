# External Bluetooth GPS (NMEA)

> Sprint 5c — package `dev.foss.expeditiongauge.gps`

## Acceptance criteria

- ✅ NMEA parser (GGA, RMC, VTG, GSA) with unit-test fixtures
- ✅ `ExternalNmeaGpsManager` over Classic SPP
- ✅ `FusedGpsLocationProvider` prefers external when valid; phone fallback
- ✅ GPS status chip: source, satellites (HDOP parsed/recorded, not shown on HUD)
- ✅ Gated by `FeatureFlags.externalGpsEnabled`

- ✅ Settings: enable toggle, device picker, forget device; GPS status chip on HUD
- ✅ `fixQuality` in recording `extrasJson`; playback highlights external GPS metadata

## Container map

| Layer | Path |
|-------|------|
| Parser | `gps/NmeaParser.kt` |
| Managers | `gps/ExternalNmeaGpsManager.kt`, `gps/FusedGpsLocationProvider.kt` |
| UI | `ui/components/gauge/GpsStatusChip.kt` |
| Playback | `playback/SampleGpsMetadata.kt` |

## Smoke scenario

1. Pair Garmin GLO 2 or Dual XGPS
2. Settings → External GPS → select device
3. Chip shows EXTERNAL + sat count; speed/HDG prefer external fix
