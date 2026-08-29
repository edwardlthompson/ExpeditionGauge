# Feature: mark-event-voice

> A mark event can store a local voice-note URI in its payload.

## Acceptance criteria

- ✅ `audioUri` is written into payload JSON without org.json
- ✅ Existing tag/latG fields stay intact
- ✅ Blank URI is ignored
- ✅ i18n: none (payload only)

## Smoke scenario

1. Given the user marks an event and records a short note
2. When the payload is saved
3. Then Relive can read `audioUri` from the mark

## Container map

| Layer | Path |
|-------|------|
| Logic | `markeventvoice/MarkEventVoice.kt` |
| Tests | `app/src/test/.../markeventvoice/` |
| Wiring | `SessionEventFactory` |

## Tests

- Automated: yes — `MarkEventVoiceTest`
- Coverage: attach, read, replace

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
