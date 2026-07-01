# ADR-0011: Session media attachments storage

- **Status:** Accepted
- **Date:** 2026-06-30
- **Deciders:** ExpeditionGauge team

## Context

Drivers want geotagged-style photo waypoints on the telemetry timeline without bloating Room or exporting proprietary gallery SDKs.

## Decision

1. **Room v4 `session_media` table** — foreign key to `recording_sessions` with `ON DELETE CASCADE`.
2. **App-private files** under `filesDir/sessions/{id}/media/` exposed via existing `FileProvider`.
3. **Timestamp association** — `timestampMs` captured from active session clock / telemetry at attach time.
4. **Compression presets** in DataStore (`ORIGINAL`, `BALANCED`, `COMPACT`) for JPEG only; video copied as-is in v2.4.0.
5. **Scrubber integration** — dedicated marker type; viewer sheet on tap.

## Consequences

- Session delete must remove media directory (repository + DAO cascade).
- Export ZIP can include media folder in a later sprint.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Store URIs only (gallery links) | Broken links after user deletes gallery items |
| Room BLOB columns | DB size + migration pain |
| CameraX dependency | Heavier stack for v2.4.0 scope |
