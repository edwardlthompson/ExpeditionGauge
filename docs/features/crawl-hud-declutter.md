# Feature: crawl-hud-declutter

> Hide GPS extras and heading on the phone HUD while crawl mode is on.

## Acceptance criteria

- ✅ Crawl on → hide lat/lon, sats, clock, heading, altitude
- ✅ Speed, pedals, alerts, and links stay visible
- ✅ Crawl off → existing preset visibility
- ✅ i18n: none

## Smoke scenario

1. Given recording mode is Crawling
2. When the telemetry cube is visible
3. Then only speed (and non-GPS chrome) remains

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
