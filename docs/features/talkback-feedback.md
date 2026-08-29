# Feature: talkback-feedback

> Announce About bug/feature buttons for TalkBack.

## Acceptance criteria

- ✅ Bug and feature have spoken labels
- ✅ About buttons use those content descriptions
- ✅ i18n: existing feedback strings

## Smoke scenario

1. Given TalkBack is on
2. When About is focused
3. Then Report a bug is spoken

## Container map

| Layer | Path |
|-------|------|
| Logic | `talkbackfeedback/TalkBackFeedback.kt` |
| Tests | `app/src/test/.../talkbackfeedback/` |
| Wiring | `AboutScreen` |

## Tests

- Automated: yes — `TalkBackFeedbackTest`
- Coverage: bug/feature labels

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
