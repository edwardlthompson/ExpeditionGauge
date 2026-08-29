# Feature: aa-inclinometer-audio

> Pitch/roll alert beeps use the Android Auto navigation-guidance audio route while Drive is open.

## Acceptance criteria

- ✅ AA session live + pitch/roll → `USAGE_ASSISTANCE_NAVIGATION_GUIDANCE` AudioTrack beep
- ✅ Other alert types (and no AA session) keep the existing media ToneGenerator path
- ✅ Session flag is set on car start/stop (no ExpeditionGaugeApp wiring)
- ✅ i18n: none (audio only)

## Smoke scenario

1. Given Drive HUD is open and pitch exceeds the limit
2. When a beep plays
3. Then the car ducks media and the chime comes from the head-unit nav route

## Container map

| Layer | Path |
|-------|------|
| Logic | `app/.../car/aainclineaudio/AaInclinometerAudio.kt` |
| View | `AlertFeedback` tone branch |
| Tests | `app/src/test/.../aainclineaudio/` |
| Wiring | `CarAppBridgeRegistry.sessionLive` |

## Tests

- Automated: yes — `AaInclinometerAudioTest`
- Coverage: route gate; sample count; nav usage

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
