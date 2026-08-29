# Feature: aa-parked-voice

> Android Auto Record/Stop speaks a nav-guidance confirmation only while parked.

## Acceptance criteria

- ✅ Parked Record/Stop speaks “Recording” / “Stopped” on the car nav-audio route
- ✅ Moving Record/Stop stays silent (button still works)
- ✅ i18n: none (fixed English voice phrases)

## Smoke scenario

1. Given the vehicle is parked
2. When Record is pressed on Drive
3. Then the head unit says “Recording”

## Container map

| Layer | Path |
|-------|------|
| Logic | `car/aaparkedvoice/AaParkedVoice.kt` |
| View | Drive Record chrome + `AlertTts` |
| Tests | `car/src/test/.../aaparkedvoice/` |
| Wiring | `CarAppBridge.speakParkedVoice` |

## Tests

- Automated: yes — `AaParkedVoiceTest`
- Coverage: announce gate; start/stop phrases

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
