# Feature: vin-last6

> Mode 09 VIN, last-6 only on the HUD. Never persist or display the full VIN.

## Acceptance criteria

- ✅ Request `0902` once per connection (not every rescan)
- ✅ HUD shows `VIN …XXXXXX` (last 6)
- ✅ `NO DATA` hides the line
- ✅ i18n: `vin_last6_*`

## Smoke scenario

1. Given Mode 09 returns a 17-character VIN
2. When the phone HUD is visible
3. Then a status line shows only the last six characters

## Container map

| Layer | Path |
|-------|------|
| Logic | `vinlast6/VinLast6.kt` |
| Adapter | `obd/Elm327Vin.kt` |
| View | `ui/phonehuddtc/PhoneHudImLine.kt` status lines |
| Tests | `src/test/.../vinlast6/` |
| Wiring | `ObdHudState` + `ObdPollLoop` once-per-connect |

## Tests

- Automated: yes — `VinLast6Test`
- Coverage: 4902 parse; last-6; NO DATA

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
