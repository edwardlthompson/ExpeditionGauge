# Feature: ford-mode22-catalog

> Curated U222 / Expedition Mode 22 PID catalog. Polling temps is the next row.

## Acceptance criteria

- ✅ Catalog lists APP/TP plus TFT/EGT Mode 22 PIDs with parse scale/offset
- ✅ Throttle discovery uses catalog throttle commands (`2209D4`, `220911`, `221340`)
- ✅ Settings shows the catalog (command + label)
- ✅ i18n: `ford_mode22_*`

## Smoke scenario

1. Given Settings → Hardware
2. When the driver opens Ford Mode 22
3. Then `2209D4 APP` and `221E1C TFT` are listed

## Container map

| Layer | Path |
|-------|------|
| Logic | `fordmode22/FordMode22Catalog.kt` |
| View | `ui/fordmode22/FordMode22CatalogDialog.kt` |
| Tests | `src/test/.../fordmode22/` |
| Wiring | `ObdThrottleQuery` + Settings hardware button |

## Tests

- Automated: yes — `FordMode22CatalogTest`
- Coverage: throttle commands; APP/TFT parse; unknown command

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
