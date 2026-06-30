# ADR-0008: External Bluetooth GPS via NMEA SPP

**Status:** Accepted  
**Date:** 2026-06-29  
**Sprint:** 5c

## Context

Phone GPS degrades under cover and at high dynamic range. External receivers (Garmin GLO 2, Dual XGPS) output standard NMEA over Bluetooth Classic SPP.

## Decision

- **`NmeaParser`** for GGA, RMC, VTG, GSA sentences
- **`ExternalNmeaGpsManager`** reads SPP stream on `Dispatchers.IO`
- **`FusedGpsLocationProvider`** prefers external when fix valid and not stale (>2 s); phone GPS always running as fallback
- **`ClassicBluetoothBudget`**: max 2 SPP (OBD + external GPS)
- Telemetry exposes `gpsSource`, `hdop`, `numSatellites`, `fixQuality`
- Gated by `FeatureFlags.externalGpsEnabled` (default off)

## Consequences

- Improved velocity heading for drift β and map route
- RTK/u-blox ZED-F9P deferred to v2 polish
