# Feature: Session Metadata

> Sprint 9 — per-session notes, tags, vehicle setup, photo attachment.

## Acceptance criteria

- ✅ User can attach notes, driver name, conditions, tags to a recording session
- ✅ Photo attach stub stores local URI placeholder (CameraX/TakePicture in ADB validation)
- ✅ Search sessions by note text or tag
- ✅ JSON export includes metadata block
- ✅ Offline: all metadata stored locally in Room

## Smoke scenario

1. Given a completed recording session
2. When user edits metadata and adds tag "offroad"
3. Then search for "offroad" returns the session and export JSON contains tags

## Container map

| Layer | Path |
|-------|------|
| Logic | `recording/SessionMetadata.kt`, `recording/SessionMetadataRepository.kt` |
| Data | `data/db/entities/RecordingSessionEntity.kt` |
| Photo | `recording/SessionPhotoCapture.kt`, `recording/SessionPhotoStub.kt` |
| UI | `ui/playback/SessionMetadataEditScreen.kt`, `ui/playback/SessionListScreen.kt` |
| Tests | `recording/SessionMetadataTest.kt`, `export/ExportMetadataTest.kt` |

## Definition of Done

- Room columns populated; FeatureFlags.sessionMetadataEnabled gates UI
- Unit tests pass; assembleDebug succeeds
