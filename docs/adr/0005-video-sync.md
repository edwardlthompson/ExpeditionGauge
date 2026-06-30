# ADR-0005: Video Sync and Burn-In Export (v2)

- **Status:** Accepted
- **Date:** 2026-06-29
- **Deciders:** ExpeditionGauge team

## Context

Drift and track sessions benefit from synchronized onboard video with telemetry overlay. Full MediaCodec burn-in pipeline is deferred; Sprint 18 establishes architecture and stub API.

## Decision

1. **`VideoSyncEngine`** — stub interface for record/import, timestamp alignment, and offset UI.
2. Sync clock driven by **`PlaybackEngine`** scrubber index; video frame seek offset stored per session.
3. Burn-in export via local **MediaCodec** pipeline (future); no cloud upload.
4. ADR gate required before enabling full v2 video features in release builds.

## Consequences

- Sprint 18 ships stub only; UI shows "coming soon" for import/sync.
- Recording and playback remain functional without video.
- Full implementation extends `VideoSyncEngine` without breaking callers.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Third-party proprietary video SDK | Violates FOSS policy |
| Cloud transcode | Privacy / offline-first conflict |
| Inline video in Room BLOB | Storage and migration complexity |
