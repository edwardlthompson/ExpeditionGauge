# Feature: Heatmap Overlay

> Sprint 11 — route intensity coloring for latG, β, slipRatio.

## Acceptance criteria

- ✅ Toggle heatmap metric: latG, drift angle, slip ratio
- ✅ Precomputed segment intensities in Kotlin (GPU-friendly buckets)
- ✅ Legend bar showing intensity scale
- ✅ Does not replace base velocity-path polyline

## Smoke scenario

1. Given playback with logged drift data
2. When user selects β heatmap
3. Then high-drift segments show warmer colors

## Container map

| Layer | Path |
|-------|------|
| Logic | `ui/playback/RouteHeatmapLayer.kt` |
| Controls | `RouteHeatmapControls`, `HeatmapLegend` composables |

## Definition of Done

- FeatureFlags.heatmapOverlayEnabled gates overlay
- Fallback to discrete color buckets (no runtime MapLibre expression dependency)
