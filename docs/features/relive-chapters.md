# Feature: relive-chapters

> Numbered Relive chapters from mark events.

## Acceptance criteria

- ✅ Untagged marks become Chapter N
- ✅ Payload `tag` becomes the chapter title
- ✅ Chapter list jumps Relive to the mark
- ✅ i18n: `relive_chapters_title`

## Smoke scenario

1. Given two mark events, one tagged "apex"
2. When Relive opens
3. Then chapters list shows "apex" and "Chapter 2"

## Container map

| Layer | Path |
|-------|------|
| Logic | `relivechapters/ReliveChapters.kt` |
| View | `ui/relivechapters/ReliveChapterList.kt` |
| Tests | `app/src/test/.../relivechapters/` |
| Wiring | `PlaybackScreenContent` |

## Tests

- Automated: yes — `ReliveChaptersTest`
- Coverage: default numbering; tag parse

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
