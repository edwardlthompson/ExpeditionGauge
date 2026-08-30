# Feature: parked-idle-dim

> **Removed.** The HUD no longer force-dims the window when parked.

Auto brightness (`ambient-autodim`) and the system brightness override handle dimming. Parked vs moving is still used for keep-awake and Android Auto parked screens (`KeepAwakeMoving`).

## Acceptance criteria

- ✅ Parked/unknown speed does not cap window brightness
- ✅ Auto + lux follows `AmbientAutodim`
- ✅ Auto + no lux uses the system brightness override

## Smoke scenario

1. Given the phone HUD open while parked (speed < 0.5 m/s)
2. When screen brightness is set to Auto with ambient light sensor available
3. Then window brightness matches ambient lux calculation without fixed 0.12 dim cap

## Container map

| Layer | Path |
|-------|------|
| Logic | removed (`parkedidle/ParkedIdleDim.kt`) |
| Wiring | `Theme` → `screenBrightnessFor` only |
## Tests

- Automated: yes — `ScreenBrightnessTest`, `AmbientAutodimTest`
- Coverage: Day / Night / Auto; lux floor

## Fallback validation

- Why tests are not feasible: N/A
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
