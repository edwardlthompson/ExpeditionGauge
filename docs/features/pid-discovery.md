# Feature: pid-discovery

> Mode 01 PID-support wizard. Off the connect-timeout path.

## Acceptance criteria

- ✅ Settings → Discover PIDs queues `0100` / `0120` / `0140` on the poll loop
- ✅ Dialog lists supported PIDs in hex; Apply enables matching `ObdPidConfig` bits
- ✅ Empty / not connected keeps toggles unchanged
- ✅ i18n: `pid_discovery_*`

## Smoke scenario

1. Given OBD is connected
2. When the driver taps Discover PIDs and Apply
3. Then RPM/speed/load (etc.) match the ECU bitmap

## Container map

| Layer | Path |
|-------|------|
| Logic | `piddiscovery/PidDiscovery.kt` |
| Adapter | `obd/Elm327PidDiscovery.kt` |
| View | `ui/piddiscovery/PidDiscoveryDialog.kt` |
| Tests | `src/test/.../piddiscovery/` |
| Wiring | Settings hardware + `ObdHudState` latch |

## Tests

- Automated: yes — `PidDiscoveryTest`
- Coverage: apply known PIDs; summary hex; empty set

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
