# Feature: aa-custom-canvas

> Android Auto Drive HUD prefers the NavigationTemplate surface canvas.

## Acceptance criteria

- ✅ Surface pending or live → NavigationTemplate canvas
- ✅ Surface attach failed → Pane fallback
- ✅ i18n: none

## Smoke scenario

1. Given the head unit grants a surface
2. When Drive opens
3. Then the custom canvas HUD paints; a failed surface attach uses the Pane HUD

## Container map

| Layer | Path |
|-------|------|
| Logic | `car/aacanvas/AaCustomCanvas.kt` |
| View | `DriveMapHudScreen` template pick |
| Tests | `car/src/test/.../aacanvas/` |
| Wiring | `onGetTemplate` |

## Tests

- Automated: yes — `AaCustomCanvasTest`
- Coverage: pending / live / failed

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
