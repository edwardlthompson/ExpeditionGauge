# Feature: colorblind-hud

> Color-blind HUD palettes for deuteranopia, protanopia, and tritanopia.

## Acceptance criteria

- ✅ Settings cycle: Default → Deuteranopia → Protanopia → Tritanopia
- ✅ Red/green-safe alert red (blue or orange) and yellow
- ✅ Shift-light and I/M “not ready” use the active palette
- ✅ i18n: `colorblind_hud_*`

## Smoke scenario

1. Given the default HUD
2. When the user cycles to Deuteranopia
3. Then the shift-light uses blue instead of red

## Container map

| Layer | Path |
|-------|------|
| Logic | `colorblind/ColorblindHud.kt` |
| Store | `settings/ColorblindHudStore.kt` |
| View | `ui/colorblind/ColorblindHudField.kt` |
| Tests | `src/test/.../colorblind/` |
| Wiring | Theme `LocalColorblindHud` |

## Tests

- Automated: yes — `ColorblindHudTest`
- Coverage: cycle; remapped alert colors

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
