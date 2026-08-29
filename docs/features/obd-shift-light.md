# Feature: obd-shift-light

> HUD shift-light when OBD RPM reaches redline. Uses alert `maxRpm` when set.

## Acceptance criteria

- ✅ Lamp on when RPM ≥ threshold (default 5500, or Settings alert max RPM)
- ✅ Hidden when RPM is missing or under threshold
- ✅ TalkBack: `shift_light_cd`
- ✅ i18n: `shift_light_*`

## Smoke scenario

1. Given OBD RPM is 5600
2. When the phone HUD is visible
3. Then a red shift-light bar appears above the pedal bar

## Container map

| Layer | Path |
|-------|------|
| Logic | `shiftlight/ShiftLight.kt` |
| View | `ui/shiftlight/ShiftLightLamp.kt` |
| Tests | `src/test/.../shiftlight/` |
| Wiring | `TelemetryHudCube` ≤10 lines |

## Tests

- Automated: yes — `ShiftLightTest`
- Coverage: null RPM; default threshold; configured threshold

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
