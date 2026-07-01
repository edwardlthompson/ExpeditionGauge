# MapLibre 3D terrain and tile sources

> Sprint 26 reference for playback map + flyover style alignment.

## Base style

Playback and flyover preview use the FOSS demo style (no API key):

```
https://demotiles.maplibre.org/style.json
```

Pinned in `PlaybackMapView` and `MapLibreFlyoverRenderer.STYLE_URI`.

## 3D camera (live preview)

| Property | Flyover default | Playback follow |
|----------|-----------------|-----------------|
| Pitch | 55° | 0° |
| Bearing | `bodyYawDeg` / heading | same |
| Zoom | 15 | ≥ 14 |

MapLibre Compose `CameraPosition` supports `pitch` for tilted terrain view when the style includes terrain (demo tiles are flat; export uses canvas perspective for elevation exaggeration).

## Tile sources (v2.8.0)

| Source | URL | Notes |
|--------|-----|-------|
| Demo vector | `demotiles.maplibre.org` | Default; offline-friendly for dev |
| Terrain raster | *deferred* | Offline terrain packs in BUILD_PLAN deferred list |

Export pipeline does **not** fetch tiles at encode time — deterministic canvas frames only.

## FOSS constraints

- No Mapbox token or Google tiles
- No proprietary SDK beyond MapLibre Compose (already pinned in `gradle.lockfile`)
