# Feature: foss-map-styles

> Extra FOSS basemap styles besides the MapLibre demo tiles.

## Acceptance criteria

- ✅ Catalog: demo, OpenFreeMap Liberty, OpenFreeMap Bright
- ✅ Cycle wraps around
- ✅ Terrain on still uses the hillshade URL
- ✅ Playback `MapStyleUrls.DEMO_STYLE` reads the selected style
- ✅ i18n: `foss_map_style`

## Smoke scenario

1. Given Settings → Maps
2. When the style button is tapped
3. Then the selected id advances to the next FOSS style

## Container map

| Layer | Path |
|-------|------|
| Logic | `fossmapstyles/FossMapStyles.kt` |
| Store | `settings/FossMapStyleStore.kt` |
| Tests | `app/src/test/.../fossmapstyles/` |
| Wiring | `MapStyleUrls`, `SettingsMapOptions` |

## Tests

- Automated: yes — `FossMapStylesTest`
- Coverage: cycle; terrain URL

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
