# 3D route flyover video

> Sprint 26 — perspective flyover MP4 from session GPS + telemetry.

## Overview

**Create 3D Video** exports a 30 s (configurable) flyover clip with a pitched camera path along the recorded route, speed/elevation HUD, and optional β/latG route coloring plus photo waypoint markers.

Live playback preview uses MapLibre with the same style URI; offline export uses canvas perspective frames (see ADR-0012).

## Architecture

| Layer | Path |
|-------|------|
| Terrain docs | `docs/design/maplibre-3d-terrain.md` |
| Camera path | `flyover/FlyoverCameraPath.kt` |
| Renderer | `flyover/MapLibreFlyoverRenderer.kt` |
| Overlay | `flyover/FlyoverOverlay.kt` |
| Export | `flyover/FlyoverVideoExporter.kt` |
| Worker | `flyover/FlyoverVideoExportWorker.kt` + thermal guard |
| UI | `ui/playback/FlyoverExportPanel.kt` |

## Acceptance criteria

- Camera keyframes follow route bearing with 55° pitch
- Overlay shows speed (km/h) + elevation (m)
- Enhanced mode: route segment color from latG; photo markers at media timestamps
- WorkManager progress; thermal throttle on Warning/Critical
- Hidden when `FeatureFlags.flyover3dEnabled` is false

## Feature flag

`project.config.json` → `sprints.v2_3d_flyover` → `FeatureFlags.flyover3dEnabled`

## ADB

`flyover-video-export` — create 30 s flyover, verify complete + share.

## Definition of Done

- `check-v2-flyover-gate.sh` green
- Unit tests in `FlyoverCameraPathTest.kt`
