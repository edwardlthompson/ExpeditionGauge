# Feature: dtc-clear

> Parked Mode 04 clear with confirm. Never on the connect-timeout path.

## Acceptance criteria

- ✅ Clear is offered only when not recording and speed is under 0.5 m/s
- ✅ Confirm dialog; Mode 04 ACK (`44`) clears the local list
- ✅ Negative response / timeout leaves codes in place
- ✅ i18n: `dtc_clear_*`

## Smoke scenario

1. Given the vehicle is parked and DTCs are shown
2. When the driver confirms Clear
3. Then Mode 04 is queued on the poll loop and a `44` ACK empties the HUD list

## Container map

| Layer | Path |
|-------|------|
| Logic | `dtcclear/DtcClear.kt` |
| Adapter | `obd/Elm327DtcClear.kt` |
| View | `ui/phonehuddtc/PhoneHudDtcFooter.kt` confirm |
| Tests | `src/test/.../dtcclear/` |
| Wiring | `ObdClassicManager.requestClearDtcs` + poll loop |

## Tests

- Automated: yes — `DtcClearTest`
- Coverage: parked gate; 44 ACK; 7F reject

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
