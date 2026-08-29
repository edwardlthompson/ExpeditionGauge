# Feature: obd-reconnect-soak

> Prove each OBD reconnect schedules an immediate Mode 03/07 scan.

## Acceptance criteria

- ✅ Eight reconnect cycles each produce one scan
- ✅ Scans are not deferred to the 30 s fallback
- ✅ `ObdReconnectSoak.passed` is true only when scans == cycles

## Smoke scenario

1. Given a live scheduler
2. When `ObdPollLoop.pump` starts eight times
3. Then scan count is 8

## Container map

| Layer | Path |
|-------|------|
| Logic | `obdreconnect/ObdReconnectSoak.kt` |
| Tests | `src/test/.../obdreconnect/` |
| Wiring | existing `ObdDtcScanScheduler` |

## Tests

- Automated: yes — `ObdReconnectSoakTest`
- Coverage: 8-cycle soak; fail when a scan is missed

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
