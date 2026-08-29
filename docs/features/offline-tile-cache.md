# Feature: offline-tile-cache

> Finish offline tile packs by FIFO-evicting regions over the eight-pack cap.

## Acceptance criteria

- ✅ Marked regions evict oldest when count exceeds 8
- ✅ Blank keys are dropped
- ✅ Settings maps show used / max
- ✅ i18n: `offline_tile_cache_usage`

## Smoke scenario

1. Given nine cached map regions
2. When a new region is marked
3. Then only the newest eight remain

## Container map

| Layer | Path |
|-------|------|
| Logic | `offlinetilecache/OfflineTileCache.kt` |
| Tests | `app/src/test/.../offlinetilecache/` |
| Wiring | `MapTileCacheRepository.markCached` |

## Tests

- Automated: yes — `OfflineTileCacheTest`
- Coverage: evict oldest; usage label

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
