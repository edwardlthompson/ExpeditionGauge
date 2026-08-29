# Feature: terrain-toggle

> Settings can switch the basemap to a FOSS hillshade style.

## Acceptance criteria

- ✅ Off keeps the current style URL
- ✅ On uses OpenFreeMap fiord hillshade
- ✅ Preference lives in `TerrainToggleStore` (not SettingsPreferences)
- ✅ i18n: `terrain_toggle`

## Smoke scenario

1. Given Settings → Maps
2. When hillshade is enabled
3. Then style URL becomes the FOSS terrain style

## Container map

| Layer | Path |
|-------|------|
| Logic | `terraintoggle/TerrainToggle.kt` |
| Store | `settings/TerrainToggleStore.kt` |
| Tests | `app/src/test/.../terraintoggle/` |
| Wiring | `SettingsMapOptions` |

## Tests

- Automated: yes — `TerrainToggleTest`
- Coverage: on/off URL

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
