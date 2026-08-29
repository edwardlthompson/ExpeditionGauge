# Feature: freeze-frame

> Mode 02 freeze frame on the phone HUD DTC detail dialog. Off the connect-timeout path.

## Acceptance criteria

- ✅ After a non-empty Mode 03/07 scan, request `0202` plus RPM/speed/throttle/load
- ✅ Matching DTC gets a one-line freeze summary in the tap-for-title dialog
- ✅ `NO DATA` / `7F02` / empty 0202 leaves codes unchanged
- ✅ i18n: `freeze_frame_*`

## Smoke scenario

1. Given stored DTCs and a Mode 02 snapshot for P0420
2. When the driver taps the HUD DTC line
3. Then the dialog shows Freeze frame: P0420 · rpm · km/h

## Container map

| Layer | Path |
|-------|------|
| Logic | `freezeframe/FreezeFrame.kt` |
| Adapter | `obd/Elm327FreezeFrame.kt` |
| View | `ui/phonehuddtc/PhoneHudDtcFooter.kt` |
| Tests | `src/test/.../freezeframe/` |
| Wiring | `ObdPollLoop` scan attach (≤10 lines) |

## Tests

- Automated: yes — `FreezeFrameTest`
- Coverage: 4202 DTC; Mode 02 PIDs; attach match; NO DATA

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
