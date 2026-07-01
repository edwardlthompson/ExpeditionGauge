# Sharing polish

> Sprint 27 — stats card + rich share preview for exported session videos.

## Overview

When sharing a playback or flyover MP4, users see a **share preview sheet** with a generated stats card (route thumb + session metrics) before the system share intent fires. Both the video and PNG card are attached via `ACTION_SEND_MULTIPLE`.

## Architecture

| Layer | Path |
|-------|------|
| Card generator | `share/ShareCardGenerator.kt` |
| Share intent | `share/ShareExportLauncher.kt` |
| Preview UI | `ui/share/SharePreviewSheet.kt` |
| Wiring | `ui/navigation/AppScreenSessionRoutes.kt` |

## Acceptance criteria

- Card includes route thumb, session name, duration, peak latG, max β, slip events
- Preview sheet shows card image + video filename before share
- Share intent grants read URI permission for video + PNG
- Hidden when `FeatureFlags.sharingPolishEnabled` is false (direct single-file share)

## Feature flag

`project.config.json` → `sprints.v2_sharing_polish` → `FeatureFlags.sharingPolishEnabled`

## ADB

`sharing-video-card` — export playback video, open share preview, confirm share intent.

## Definition of Done

- Gate: `scripts/check-v2-sharing-gate.sh`
- Unit test: `ShareCardGeneratorTest`
- ADB scenario passes on dev device
