# Feature: gear-estimate

> HUD gear estimate from OBD RPM and vehicle speed. No extra PIDs.

## Acceptance criteria

- ✅ Estimate gears 1–6 from km/h per 1000 RPM bands
- ✅ Hidden when RPM &lt; 800 or speed &lt; 3 km/h
- ✅ TalkBack: `gear_estimate_cd`
- ✅ i18n: `gear_estimate_*`

## Smoke scenario

1. Given RPM is 2000 and speed is 58 km/h
2. When the phone HUD is visible
3. Then the status line shows `Gear 4`

## Container map

| Layer | Path |
|-------|------|
| Logic | `gearestimate/GearEstimate.kt` |
| View | HUD `statusLines` |
| Tests | `src/test/.../gearestimate/` |
| Wiring | `AppScreenDashboardRoute` ≤10 lines |

## Tests

- Automated: yes — `GearEstimateTest`
- Coverage: 1–6 bands; idle/missing hide

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
