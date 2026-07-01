# Activity library

> Sprint 24 — organize sessions by activity type with route thumbnails, filters, and home quick stats.

## Overview

Sessions carry an **activity type** (Drive, Off-road, Track, Towing, Other). The session list shows GPS route thumbnails, filter chips by type, and text search. The dashboard shows a quick-stats strip when not recording.

## Architecture

| Layer | Path |
|-------|------|
| Type enum | `recording/ActivityType.kt` |
| Entity | `RecordingSessionEntity.activityType` (Room v5) |
| Thumbnails | `stats/SessionThumbnailGenerator.kt` — decimated lat/lon polyline |
| List UI | `ui/playback/SessionListScreen.kt` — filter chips + search |
| Card | `ui/stats/RichSessionCard.kt` — route thumb + activity label |
| Home strip | `ui/dashboard/HomeQuickStatsStrip.kt` |
| Metadata | `SessionMetadataEditScreen.kt` — activity type chips |

## Acceptance criteria

- Activity type persisted on `RecordingSessionEntity` and editable in metadata screen
- Route thumbnail on session cards when ≥2 GPS samples exist
- Filter chips: All + each activity type; combines with search
- Home quick-stats: session count, total duration, best lap (when library enabled)
- Hidden when `FeatureFlags.activityLibraryEnabled` is false

## Feature flag

`project.config.json` → `sprints.v2_activity_library` → `FeatureFlags.activityLibraryEnabled`

## ADB

`library-filter-tag` — open sessions, verify route thumbnail, set OFFROAD type, filter by OFFROAD chip.

## Definition of Done

- `check-v2-library-gate.sh` green
- Unit tests in `SessionThumbnailGeneratorTest.kt`
