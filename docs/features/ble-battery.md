# Feature: ble-battery

> Show BLE accessory battery as low / warn / ok bands.

## Acceptance criteria

- ✅ Percent clamps 0–100
- ✅ ≤15 low, ≤35 warn, else ok
- ✅ Null is unknown
- ✅ i18n: none (band ids)

## Smoke scenario

1. Given a TPMS report of 12%
2. When the HUD draws the battery icon
3. Then the band is low

## Container map

| Layer | Path |
|-------|------|
| Logic | `blebattery/BleBattery.kt` |
| Tests | `app/src/test/.../blebattery/` |
| Wiring | `TpmsBatteryIcon` bands match this policy |

## Tests

- Automated: yes — `BleBatteryTest`
- Coverage: bands; clamp

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
