# Feature: recording-preroll

> Record prepends the last 5 seconds of live telemetry so the session includes the lead-in.

## Acceptance criteria

- ✅ Live samples are retained for 5 s while not recording
- ✅ Start recording writes the preroll, then continues the live loop
- ✅ Samples older than the window are dropped
- ✅ i18n: none (buffer only)

## Smoke scenario

1. Given the HUD has been live for 10 s
2. When Record starts
3. Then the session file includes ~5 s of samples before the tap

## Container map

| Layer | Path |
|-------|------|
| Logic | `recordingpreroll/RecordingPreroll.kt` |
| View | none |
| Tests | `app/src/test/.../recordingpreroll/` |
| Wiring | `RecordingWriter` |

## Tests

- Automated: yes — `RecordingPrerollTest`
- Coverage: window eviction; drain clears the buffer

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
