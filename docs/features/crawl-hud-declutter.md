# Feature: crawl-hud-declutter

> Hide GPS extras and heading on the phone HUD while a CRAWL recording is running.

## Acceptance criteria

- ✅ CRAWL recording on → hide lat/lon, sats, clock, heading, altitude
- ✅ Speed, pedals, alerts, and links stay visible
- ✅ Not recording (including Offroad idle) → existing preset visibility
- ✅ i18n: none

## Smoke scenario

1. Given Offroad is selected and no recording is running
2. When the telemetry cube is visible
3. Then HDG, elevation, and GPS extras stay visible
4. When a CRAWL recording starts
5. Then only speed (and non-GPS chrome) remains

## Container map

| Layer | Path |
|-------|------|
| Logic | `crawlhud/CrawlHudDeclutter.kt` |
| View | `TelemetryHudCube` |
| Tests | `src/test/.../crawlhud/` |
| Wiring | `HudCubeLayout.hideGpsExtras` |
## Tests

- Automated: yes — `CrawlHudDeclutterTest`
- Coverage: crawl on/off visibility flags

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
