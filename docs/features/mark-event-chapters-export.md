# Feature: mark-event-chapters-export

> HTML session share includes a chapter list from mark events.

## Acceptance criteria

- ✅ Chapter table lists title and timestamp
- ✅ Empty marks omit the section
- ✅ HtmlSummaryExporter embeds the block
- ✅ i18n: none (export HTML)

## Smoke scenario

1. Given a session with a tagged mark
2. When HTML summary is shared
3. Then the page has a Chapters table with that title

## Container map

| Layer | Path |
|-------|------|
| Logic | `markeventchapters/MarkEventChaptersExport.kt` |
| Tests | `app/src/test/.../markeventchapters/` |
| Wiring | `HtmlSummaryExporter` |

## Tests

- Automated: yes — `MarkEventChaptersExportTest`
- Coverage: titled row

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
