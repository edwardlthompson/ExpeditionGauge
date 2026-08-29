# Feature: aa-parked-library

> Parked Android Auto Drive HUD opens a recorded-session list when the telemetry cube is tapped.

## Acceptance criteria

- ✅ Tap the telemetry cube while parked → ListTemplate of session name + duration
- ✅ Moving does not open the library
- ✅ Empty library shows “No sessions yet”
- ✅ i18n: none (session names stay as recorded)

## Smoke scenario

1. Given the vehicle is parked and one session exists
2. When the driver taps the middle HUD cube
3. Then AA shows the session name and duration, and Back returns to Drive

## Container map

| Layer | Path |
|-------|------|
| Logic | `car/aaparkedlibrary/AaParkedLibrary.kt` |
| View | `car/ui/AaParkedLibraryScreen.kt` |
| Tests | `car/src/test/.../aaparkedlibrary/` |
| Wiring | telemetry-cube tap + `CarAppBridge.parkedLibraryRows` |

## Tests

- Automated: yes — `AaParkedLibraryTest`, telemetry hit-test
- Coverage: parked gate; duration labels; row cap; blank name fallback

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
