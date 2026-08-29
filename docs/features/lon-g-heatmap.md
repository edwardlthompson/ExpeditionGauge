# Feature: lon-g-heatmap

> Relive heatmap can color the route by brake/accel lonG.

## Acceptance criteria

- ✅ `HeatmapMetric.LON_G` uses abs(lonAccel)
- ✅ Color buckets match latG thresholds
- ✅ HUD chip is labelled lonG
- ✅ i18n: `heatmap_lon_g`

## Smoke scenario

1. Given Relive heatmap controls
2. When lonG is selected
3. Then the route colors by longitudinal G

## Container map

| Layer | Path |
|-------|------|
| Logic | `playback/RouteHeatmapLayer.kt` |
| View | `ui/playback/RouteHeatmapLayer.kt` |
| Tests | `app/src/test/.../playback/LonGHeatmapTest.kt` |

## Tests

- Automated: yes — `LonGHeatmapTest`
- Coverage: intensity and bucket

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
