# Media attachments

> Sprint 22 — timestamped photos and videos linked to recording sessions.

## Overview

Attach photos (and short video clips) during or after a drive. Each attachment stores the capture time aligned to session telemetry so playback scrubber markers open a media viewer at the correct moment.

## Architecture

- **`SessionMediaEntity`** (Room v4) — metadata rows with `sessionId`, `timestampMs`, file path, MIME type.
- **`SessionMediaRepository`** — copy/compress into app-private storage; cascade delete with session.
- **`CarAppBridge` / telemetry** — capture timestamp from `TelemetryBus.snapshots` at attach time.
- **Playback** — `ScrubberMarkerType.MEDIA_ATTACHMENT` + `MediaViewerSheet`.

## User flow

1. Start **Record** on the dashboard.
2. Tap **Attach media** → camera or gallery.
3. Stop recording → open **Sessions** → **Play**.
4. Tap a media marker on the scrubber strip to preview the photo.
5. **Settings** shows total media storage and compression preset.

## Data model

Files live under `filesDir/sessions/{sessionId}/media/` — never BLOBs in Room (ADR-0011).

Legacy `RecordingSessionEntity.photoUri` remains for session cover thumbnails; timed attachments use `session_media` table.

## Feature flag

`FeatureFlags.mediaAttachmentsEnabled` (`project.config.json` → `sprints.v2_media_attach`).

## FOSS constraints

- CameraX not required — `TakePicture` + `GetContent` contracts only.
- Local JPEG compression via `Bitmap.compress`; no cloud upload.

## ADB

`media-attach-recording` — attach stub photo while recording, verify scrubber marker, delete session removes files.
