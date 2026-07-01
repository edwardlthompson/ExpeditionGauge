# ADR-0012: Playback video export capture path

- **Status:** Accepted
- **Date:** 2026-06-30
- **Deciders:** ExpeditionGauge team

## Context

Sprint 18 burn-in exports telemetry onto an **imported** session MP4. Relive users also need a **synthetic** MP4 from GPS + telemetry alone (no camera file), rendered during playback review.

## Decision

1. **`VideoFrameCapturer`** — Canvas route polyline from decimated lat/lon samples; no MapLibre snapshot in v2.7.0 (offline-safe, deterministic tests).
2. **`PlaybackVideoExporter`** — frame loop + reuse **`VideoBurnInEncoder`** MediaCodec mux (10 fps H.264).
3. **Overlay** — extend `VideoOverlayCompositor` with speed, latG, β, pitch, roll per frame.
4. **Background** — `WorkManager` unique work per session export; progress via `setProgress`; share via existing `FileProvider` cache path.
5. Distinct from burn-in: no `MediaMetadataRetriever` source video.

## Consequences

- Export quality is schematic route + HUD, not satellite tiles (3D flyover is Sprint 26).
- Thermal throttling deferred to Sprint 26; clip length capped in UI (default 2 min).

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| MapLibre offscreen render | Heavier; tile network dependency |
| FFmpeg CLI | Non-FOSS bundling / APK size |
| In-process only (no WorkManager) | ANR risk on 2-min encode |
