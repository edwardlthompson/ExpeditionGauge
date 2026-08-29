# Feature: offroad-hold-bars

> Offroad / crawl inclinometer bars hold at the session peak pitch and roll.

## Acceptance criteria

- ✅ Crawl mode or Offroad preset → bars use the larger of live vs peak
- ✅ Corner labels stay on the live pitch/roll
- ✅ Other presets without crawl keep live bars
- ✅ i18n: none (reuses peak strings)

## Smoke scenario

1. Given Offroad or crawl is active and pitch peaked at 20°
2. When the vehicle returns to 5°
3. Then the pitch bar stays at 20° and the label reads 5°

## Container map

| Layer | Path |
|-------|------|
| Logic | `offroadhold/OffroadHoldBars.kt` |
| View | `InclinometerGauge` bar values |
| Tests | `src/test/.../offroadhold/` |
| Wiring | `HudCubeLayout` inclinometer |

## Tests

- Automated: yes — `OffroadHoldBarsTest`
- Coverage: active flags; hold vs live

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
