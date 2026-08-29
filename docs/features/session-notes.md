# Feature: session-notes

> Per-session notes persist in Room and match library search.

## Acceptance criteria

- ✅ Notes trim on save and drop blank strings
- ✅ Library search matches note text case-insensitively
- ✅ Edit UI: `SessionMetadataEditScreen`
- ✅ i18n: existing metadata strings

## Smoke scenario

1. Given a finished session
2. When the user saves notes "wet trail"
3. Then library search for "trail" returns that session

## Container map

| Layer | Path |
|-------|------|
| Logic | `sessionnotes/SessionNotes.kt` |
| Existing | `recording/SessionMetadata.kt`, `ui/playback/SessionMetadataEditScreen.kt` |
| Tests | `app/src/test/.../sessionnotes/` |

## Tests

- Automated: yes — `SessionNotesTest`, `SessionMetadataTest`
- Coverage: normalize; case-insensitive match

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
