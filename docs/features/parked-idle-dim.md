# Feature: parked-idle-dim

> Extra-dim the HUD when parked to reduce burn-in.

## Acceptance criteria

- ✅ Speed &lt; 0.5 m/s or unknown → parked
- ✅ Parked brightness is capped at 0.12 (including Auto override)
- ✅ Moving leaves the base brightness unchanged

## Smoke scenario

1. Given the vehicle is stationary
2. When the HUD is visible
3. Then window brightness is at the parked dim floor

## Container map

| Layer | Path |
|-------|------|
| Logic | `parkedidle/ParkedIdleDim.kt` |
| View | `ui/parkedidle/HudSpeed.kt` |
| Tests | `src/test/.../parkedidle/` |
| Wiring | Theme brightness apply |

## Tests

- Automated: yes — `ParkedIdleDimTest`
- Coverage: parked vs moving; override-none

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
