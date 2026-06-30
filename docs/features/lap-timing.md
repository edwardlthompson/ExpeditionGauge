# Feature: Lap Timing

> Sprint 10 — GPS auto lap detection, sector splits, predictive delta.

## Acceptance criteria

- ✅ Auto lap detection via start/finish line crossing (GPS)
- ✅ Up to 9 sector lines with split times
- ✅ Session best lap and theoretical best (sum of best sectors)
- ✅ Predictive delta strip on dashboard (settings toggle, default off)
- ✅ Playback lap list and sector times
- ✅ Phone-only: no OBD/IMU required

## Smoke scenario

1. Given a session with TrackConfig start/finish GeoJSON
2. When GPS track crosses the line twice at speed > 2 m/s
3. Then LapEntity rows created with valid duration

## Container map

| Layer | Path |
|-------|------|
| Logic | `timing/LapDetector.kt`, `timing/SectorSplitCalculator.kt`, `timing/PredictiveTimingEngine.kt`, `timing/LapTimingService.kt`, `timing/TrackLineBuilder.kt` |
| Data | `TrackConfigEntity`, `LapEntity`, `SectorSplitEntity` |
| UI | `ui/components/LapTimerStrip.kt`, `ui/timing/TrackSetupScreen.kt`, `ui/playback/LapListPanel.kt` |
| ADR | `docs/adr/0002-lap-timing.md` |
| Tests | `timing/*Test.kt` |

## Definition of Done

- ADR-0002 accepted; pure GPS domain (no map SDK in timing logic)
- FeatureFlags.lapTimingEnabled gates lap UI
