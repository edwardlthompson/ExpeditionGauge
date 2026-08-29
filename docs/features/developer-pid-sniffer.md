# Feature: developer-pid-sniffer

> Opt-in developer PID sniffer. Off the connect-timeout path. Blocks Mode 04 / 09.

## Acceptance criteria

- ✅ Developer screen field sends a normalized even-length hex command
- ✅ Mode `04` (clear) and Mode `09` (VIN) are rejected
- ✅ VIN payloads (`4902`) are redacted; raw text truncated to 120 chars
- ✅ i18n: `pid_sniffer_*`

## Smoke scenario

1. Given OBD is connected and developer mode is on
2. When the user sniffs `010C`
3. Then the last ELM line updates without touching VIN or Mode 04

## Container map

| Layer | Path |
|-------|------|
| Logic | `pidsniffer/PidSniffer.kt` |
| Adapter | `obd/Elm327PidSniffer.kt` |
| View | `ui/pidsniffer/PidSnifferPanel.kt` |
| Tests | `src/test/.../pidsniffer/` |
| Wiring | Developer mode + pump latch |

## Tests

- Automated: yes — `PidSnifferTest`
- Coverage: normalize; block 04/09; VIN redact

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
