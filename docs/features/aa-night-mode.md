# Feature: aa-night-mode

> Android Auto HUD dark background follows the car UI night setting.

## Acceptance criteria

- ✅ Host night or `CarContext.isDarkMode` → dark HUD
- ✅ Day + host not dark → light HUD background
- ✅ i18n: none

## Smoke scenario

1. Given the head unit is in night mode
2. When Drive opens
3. Then the canvas/Pane HUD uses the dark palette

## Container map

| Layer | Path |
|-------|------|
| Logic | `car/aanight/AaNightMode.kt` |
| View | Drive/Grid display spec |
| Tests | `car/src/test/.../aanight/` |
| Wiring | `DrivePaneTemplates` + `TelemetryGridTemplates` |

## Tests

- Automated: yes — `AaNightModeTest`
- Coverage: night; host dark; day

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
