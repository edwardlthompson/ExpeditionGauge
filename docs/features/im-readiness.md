# Feature: im-readiness

> Mode 01 PID 01 I/M monitors on the phone HUD. Off the connect-timeout path.

## Acceptance criteria

- ✅ Parse spark/compression monitor bits from `4101` A–D
- ✅ HUD shows a one-line ready / not-ready summary (even when no DTCs)
- ✅ Incomplete names are listed (max 3 + remainder)
- ✅ i18n: `im_readiness_*`

## Smoke scenario

1. Given an ELM `0101` response with EVAP incomplete
2. When the phone HUD is visible
3. Then the footer reads `I/M not ready: EVAP`

## Container map

| Layer | Path |
|-------|------|
| Logic | `imreadiness/ImReadiness.kt` |
| Hold | `imreadiness/ImReadinessHold.kt` |
| View | `ui/phonehuddtc/PhoneHudDtcFooter.kt` |
| Tests | `src/test/.../imreadiness/` |
| Wiring | `ObdPollLoop` + `ObdClassicManager` ≤10 lines |

## Tests

- Automated: yes — `ImReadinessTest`
- Coverage: spark EVAP incomplete; all-ready continuous; NO DATA

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
