# Video sync

> Sprint 18 — v2 video alignment with playback scrubber.

## Overview

Link an onboard MP4 to a recorded session, align timestamps with a manual offset, and preview synchronized video during playback. Optional burn-in export overlays speed, β, and latG via a local MediaCodec pipeline.

## Architecture

- **`VideoSyncEngine`** — import URI, persist offset per session, seek ExoPlayer to scrubber time + offset.
- **`PlaybackEngine`** — master clock via `SampleEntity.timestampMs`.
- **`VideoBurnInExporter`** — MediaMetadataRetriever frame sampling + MediaCodec encode (local only).

## User flow

1. Open **Sessions** → **Play** a recording.
2. Tap **Import video** and pick an MP4 from storage.
3. Adjust **Video offset (ms)** until overlay metrics match the scrubber (±200 ms target).
4. Optional: **Export burn-in video** shares an annotated MP4.

## Data model

`RecordingSessionEntity` stores `videoUri` and `videoOffsetMs` (no video BLOB in Room — ADR-0005).

## Feature flag

`FeatureFlags.videoSyncEnabled` (from `project.config.json` → `sprints.v2_video`).

## FOSS constraints

- AndroidX Media3 ExoPlayer only — no proprietary SDKs.
- No cloud transcode; share intent exports from app cache.

## ADB

`video-sync-drift` — verifies offset label and playback video controls visible after import path stub.
