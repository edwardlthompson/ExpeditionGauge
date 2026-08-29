# Feature: night-hud-palette

> Amber night-vision HUD when brightness is Night. Off by toggle.

## Acceptance criteria

- ✅ Night brightness + toggle on → amber-on-black HUD scheme
- ✅ Toggle off or Day/Auto → existing schemes
- ✅ Default: palette enabled
- ✅ i18n: `night_hud_*`

## Smoke scenario

1. Given brightness is Night and the palette is on
2. When the HUD is visible
3. Then surfaces use amber night-vision colors

## Container map

| Layer | Path |
|-------|------|
| Logic | `nighthud/NightHudPalette.kt` |
| Store | `settings/NightHudStore.kt` |
| View | `ui/nighthud/NightHudField.kt` |
| Tests | `src/test/.../nighthud/` |
| Wiring | Theme + Display settings |

## Tests

- Automated: yes — `NightHudPaletteTest`
- Coverage: active only when night + enabled

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
