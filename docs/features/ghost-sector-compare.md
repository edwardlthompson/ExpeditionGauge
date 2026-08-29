# Feature: ghost-sector-compare

> Compare a live lap against a ghost by sector split times.

## Acceptance criteria

- ✅ Sector rows align by `sectorIndex`
- ✅ Missing ghost sectors are omitted
- ✅ Net delta is the sum of aligned sector deltas
- ✅ Relive panel lists sector compare rows
- ✅ i18n: `ghost_lap_sector_*`

## Smoke scenario

1. Given primary and ghost laps with two sectors
2. When Relive shows ghost compare
3. Then each sector row has primary, ghost, and signed delta

## Container map

| Layer | Path |
|-------|------|
| Logic | `ghostsectorcompare/GhostSectorCompare.kt` |
| Tests | `app/src/test/.../ghostsectorcompare/` |
| Wiring | `GhostLapComparePanel` |

## Tests

- Automated: yes — `GhostSectorCompareTest`
- Coverage: index align; net delta; fastest-sector count

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
