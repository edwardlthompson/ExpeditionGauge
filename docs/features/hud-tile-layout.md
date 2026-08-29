# Feature: hud-tile-layout

> User-configurable HUD tile order (Attitude / Telemetry / TPMS).

## Acceptance criteria

- ✅ Default order is Attitude · Telemetry · TPMS
- ✅ Settings → HUD tile layout cycles the stored order
- ✅ `HudCubeLayout` arranges visible tiles in that order
- ✅ Unknown / partial stored values fall back to the default remaining tiles
- ✅ i18n: `hud_tile_*`

## Smoke scenario

1. Given the default HUD
2. When the user cycles tile order
3. Then Telemetry is the first cube

## Container map

| Layer | Path |
|-------|------|
| Logic | `hudtile/HudTileLayout.kt` |
| Store | `settings/HudTileLayoutStore.kt` |
| View | `ui/hudtile/HudTileLayoutDialog.kt` |
| Tests | `src/test/.../hudtile/` |
| Wiring | `HudCubeLayout` + Settings hardware |

## Tests

- Automated: yes — `HudTileLayoutTest`
- Coverage: parse; cycle; arrange

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
