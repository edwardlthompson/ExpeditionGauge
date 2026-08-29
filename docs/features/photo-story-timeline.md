# Feature: photo-story-timeline

> Relive shows a horizontal photo story from media attachments.

## Acceptance criteria

- ✅ Photos sort by timestamp
- ✅ Videos are omitted from the strip
- ✅ Tapping a photo seeks Relive
- ✅ i18n: file/label text from the attachment

## Smoke scenario

1. Given two photos attached during a session
2. When Relive opens
3. Then a strip lists them in time order and seeks on tap

## Container map

| Layer | Path |
|-------|------|
| Logic | `photostory/PhotoStoryTimeline.kt` |
| View | `ui/photostory/PhotoStoryStrip.kt` |
| Tests | `app/src/test/.../photostory/` |
| Wiring | `PlaybackScreenContent` |

## Tests

- Automated: yes — `PhotoStoryTimelineTest`
- Coverage: sort; skip video; marker mapping

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
