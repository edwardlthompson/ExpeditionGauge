# Feature: aa-high-contrast

> Android Auto Drive HUD uses high-contrast color tokens when the phone toggle or system high-contrast is on.

## Acceptance criteria

- ✅ High-contrast dark: black fill, white glyphs, yellow secondary, red alerts
- ✅ High-contrast light: white fill, black glyphs
- ✅ Phone Settings high-contrast (or system `high_text_contrast_enabled`) selects the tokens
- ✅ i18n: none (palette only)

## Smoke scenario

1. Given Settings → Accessibility → High contrast is on
2. When Drive HUD paints
3. Then cube chrome is black/white (not the standard night charcoal)

## Container map

| Layer | Path |
|-------|------|
| Logic | `car/aahighcontrast/AaHighContrast.kt` |
| View | `DriveHudTheme.forDarkMode(..., highContrast)` |
| Tests | `car/src/test/.../aahighcontrast/` |
| Wiring | `AaDisplaySpec.isHighContrast` + bridge pref collect |

## Tests

- Automated: yes — `AaHighContrastTest`
- Coverage: off → standard tokens; on → max-contrast + textScale copy

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
