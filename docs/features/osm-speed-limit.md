# Feature: osm-speed-limit

> Look up an offline OSM maxspeed from local lat,lon,radius,kph zones.

## Acceptance criteria

- ✅ Parse `lat,lon,radiusKm,kph` lines
- ✅ Tightest matching zone wins
- ✅ Outside all radii returns null
- ✅ Overlay label is `{kph} km/h`
- ✅ i18n: `osm_speed_limit_overlay`

## Smoke scenario

1. Given a 30 km/h zone inside a 50 km/h zone
2. When lookup is at the center
3. Then the overlay shows 30 km/h

## Container map

| Layer | Path |
|-------|------|
| Logic | `osmspeedlimit/OsmSpeedLimit.kt` |
| Tests | `app/src/test/.../osmspeedlimit/` |
| Wiring | Settings maps heading uses overlay label helper |

## Tests

- Automated: yes — `OsmSpeedLimitTest`
- Coverage: nested zones; miss

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
