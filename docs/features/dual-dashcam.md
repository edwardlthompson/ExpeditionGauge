# Feature: dual-dashcam

> Import extra dashcam files with per-file time offsets.

## Acceptance criteria

- ✅ Clips encode as `uri|offsetMs` joined by `;`
- ✅ Primary `videoUri` stays on the session row
- ✅ Extra clips persist in `deviceConfigJson` under `dashcamExtra`
- ✅ i18n: none (URI list)

## Smoke scenario

1. Given a front camera already synced
2. When a rear file is added at -250 ms
3. Then extras parse to two clips with that offset

## Container map

| Layer | Path |
|-------|------|
| Logic | `dualdashcam/DualDashcam.kt` |
| Tests | `app/src/test/.../dualdashcam/` |
| Wiring | `VideoSyncEngine` extras key |

## Tests

- Automated: yes — `DualDashcamTest`
- Coverage: parse/encode/plus

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
