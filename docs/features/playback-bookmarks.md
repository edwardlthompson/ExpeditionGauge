# Feature: playback-bookmarks

> Relive jump list from mark-event scrubber dots.

## Acceptance criteria

- ✅ Only `MARK_EVENT` markers become bookmarks
- ✅ Duplicate sample indexes collapse
- ✅ Tapping a bookmark seeks Relive
- ✅ i18n: `playback_bookmark_mark`

## Smoke scenario

1. Given a session with two mark events
2. When Relive opens
3. Then a horizontal jump list seeks to each mark

## Container map

| Layer | Path |
|-------|------|
| Logic | `playbackbookmarks/PlaybackBookmarks.kt` |
| View | `ui/playbackbookmarks/PlaybackBookmarkList.kt` |
| Tests | `app/src/test/.../playbackbookmarks/` |
| Wiring | `PlaybackScreenContent` |

## Tests

- Automated: yes — `PlaybackBookmarksTest`
- Coverage: filter + distinct

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
