# Feature: wet-tire-alerts

> Tighten TPMS pressure, temp, and loss limits when the wet/rain profile is on.

## Acceptance criteria

- ✅ Toggle default off
- ✅ Wet: min pressure ×1.1, max temp −10 °C, loss rate ×0.8
- ✅ Dry or unset tire limits unchanged
- ✅ i18n: `alerts_wet_tire_toggle`

## Smoke scenario

1. Given a 200 kPa min pressure and the wet profile is on
2. When TPMS reports 210 kPa
3. Then a low-pressure alert fires

## Container map

| Layer | Path |
|-------|------|
| Logic | `wettire/WetTireAlerts.kt` |
| Store | `settings/WetTireStore.kt` |
| View | `ui/wettire/WetTireField.kt` |
| Tests | `src/test/.../wettire/` |
| Wiring | `resolvedAlertThresholds` + Settings |

## Tests

- Automated: yes — `WetTireAlertsTest`
- Coverage: wet tighten; dry passthrough; null limits

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
