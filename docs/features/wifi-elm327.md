# Feature: wifi-elm327

> FOSS TCP ELM327 (no Play). Same poll loop as Bluetooth SPP.

## Acceptance criteria

- ✅ Settings field accepts `host:port` (default `192.168.0.10:35000`)
- ✅ Only RFC1918 / link-local / localhost — public hosts rejected
- ✅ Stored as `tcp:host:port` in the existing OBD device preference
- ✅ Bluetooth MAC addresses are never parsed as Wi-Fi
- ✅ i18n: `wifi_elm327_*`

## Smoke scenario

1. Given the phone is on the adapter AP at `192.168.0.10:35000`
2. When the user taps Connect Wi-Fi ELM327
3. Then OBD uses the same ELM handshake and poll loop as Bluetooth

## Container map

| Layer | Path |
|-------|------|
| Logic | `wifielm/WifiElm327.kt` |
| Adapter | `obd/ObdTcp.kt` + `ObdConnectSession` |
| View | `ui/wifielm/WifiElm327Field.kt` |
| Tests | `src/test/.../wifielm/` |
| Wiring | Settings hardware + `ObdClassicManager.connect` |

## Tests

- Automated: yes — `WifiElm327Test`
- Coverage: parse default; reject public IP and BT MAC

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
