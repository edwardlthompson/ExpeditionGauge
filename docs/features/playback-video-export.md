# Playback video export

> Sprint 25 — synthetic MP4 from session telemetry + route schematic.

## Overview

Export a shareable H.264 clip from any recorded session during playback. Frames show the route polyline up to the current sample plus a telemetry HUD (speed, latG, β, pitch, roll). Encoding runs in the background via WorkManager.

Distinct from Sprint 18 **burn-in** export, which overlays telemetry onto an imported MP4.

## Architecture

| Layer | Path |
|-------|------|
| ADR | `docs/adr/0012-playback-video-export.md` |
| Settings | `export/PlaybackVideoExportSettings.kt` |
| Frames | `export/VideoFrameCapturer.kt` |
| Pipeline | `export/PlaybackVideoExporter.kt` + `video/VideoBurnInEncoder.kt` |
| Overlay | `video/VideoOverlayCompositor.kt` |
| Worker | `export/PlaybackVideoExportWorker.kt` |
| UI | `ui/playback/PlaybackExportPanel.kt` |

## Acceptance criteria

- Clip length presets: 30 s, 1 min, 2 min (default)
- Progress indicator during encode; share intent on success
- Hidden when `FeatureFlags.playbackVideoExportEnabled` is false

## Feature flag

`project.config.json` → `sprints.v2_playback_export` → `FeatureFlags.playbackVideoExportEnabled`

## ADB

`playback-video-export` — start export from playback, verify progress/complete UI.

## Definition of Done

- `check-v2-playback-export-gate.sh` green
- Unit tests in `VideoFrameCapturerTest.kt`
