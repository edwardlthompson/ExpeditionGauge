# Feature: preset-alert-thresholds

> Empty alert limits inherit defaults from the active HUD preset.

## Acceptance criteria

- ✅ User-set thresholds always win
- ✅ Track fills empty latG / RPM / speed
- ✅ Drift fills empty latG / drift / slip
- ✅ Offroad fills empty pitch / roll
- ✅ Default / Minimal add no extra limits
- ✅ i18n: `alerts_preset_hint`

## Smoke scenario

1. Given Settings speed limit is empty and the HUD preset is Track
2. When alerts are enabled
3. Then SPEED uses the Track default until the user types a value

## Container map

| Layer | Path |
|-------|------|
| Logic | `presetalerts/PresetAlertThresholds.kt` |
| View | Settings alerts hint |
| Tests | `src/test/.../presetalerts/` |
| Wiring | AlertService combine + profile |

## Tests

- Automated: yes — `PresetAlertThresholdsTest`
- Coverage: user override; Track fill; Default empty

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
