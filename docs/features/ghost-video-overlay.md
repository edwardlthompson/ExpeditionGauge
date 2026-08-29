# Feature: ghost-video-overlay

> Burn lap and ghost telemetry side by side onto exported video.

## Acceptance criteria

- ✅ Overlay lines pair lap vs ghost with a `Lap | Ghost` header
- ✅ Empty ghost list falls back to lap-only lines
- ✅ Burn-in export uses nearest ghost sample at the frame timestamp
- ✅ i18n: none (overlay labels)

## Smoke scenario

1. Given Relive has a ghost lap and a dashcam clip
2. When burn-in export runs
3. Then each frame lists lap and ghost fields together

## Container map

| Layer | Path |
|-------|------|
| Logic | `ghostvideooverlay/GhostVideoOverlay.kt` |
| Tests | `app/src/test/.../ghostvideooverlay/` |
| Wiring | `VideoBurnInExporter`, `AppScreenPlaybackRoute` |

## Tests

- Automated: yes — `GhostVideoOverlayTest`
- Coverage: paired lines; nearest ghost

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
