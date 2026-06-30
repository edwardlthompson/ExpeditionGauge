# External Bluetooth GPS (NMEA)

> Sprint 5c — package `dev.foss.expeditiongauge.gps`

## Acceptance criteria

- ✅ NMEA parser (GGA, RMC, VTG, GSA) with unit-test fixtures
- ✅ `ExternalNmeaGpsManager` over Classic SPP
- ✅ `FusedGpsLocationProvider` prefers external when valid; phone fallback
- ✅ GPS status chip: source, satellites, HDOP
- ✅ Gated by `FeatureFlags.externalGpsEnabled`

## Container map

| Layer | Path |
|-------|------|
| Parser | `gps/NmeaParser.kt` |
| Managers | `gps/ExternalNmeaGpsManager.kt`, `gps/FusedGpsLocationProvider.kt` |
| UI | `ui/components/gauge/GpsStatusChip.kt` |

## Smoke scenario

1. Pair Garmin GLO 2 or Dual XGPS
2. Settings → External GPS → select device
3. Chip shows EXTERNAL, sat count, HDOP; speed/HDG prefer external fix
