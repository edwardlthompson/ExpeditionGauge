# Feature: share-to-files

> Default share chooser prefers Files / DocumentsUI, not social apps.

## Acceptance criteria

- ✅ DocumentsUI and Files packages are preferred
- ✅ Social packages are not preferred
- ✅ Chooser seeds DocumentsUI as an initial intent
- ✅ i18n: existing share title

## Smoke scenario

1. Given a video + card share
2. When the chooser opens
3. Then Files / DocumentsUI is offered first

## Container map

| Layer | Path |
|-------|------|
| Logic | `sharetofiles/ShareToFiles.kt` |
| Tests | `app/src/test/.../sharetofiles/` |
| Wiring | `ShareExportLauncher` |

## Tests

- Automated: yes — `ShareToFilesTest`
- Coverage: package preference

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
