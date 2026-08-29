# Feature: session-map-compare

> Build two GPS polylines so two sessions can be compared on a map.

## Acceptance criteria

- ✅ Samples without lat/lon are dropped
- ✅ Left and right polylines are independent
- ✅ Comparison screen can render both
- ✅ i18n: none (geometry)

## Smoke scenario

1. Given two sessions with GPS
2. When compare opens
3. Then both polylines have only fixed points

## Container map

| Layer | Path |
|-------|------|
| Logic | `sessionmapcompare/SessionMapCompare.kt` |
| Tests | `app/src/test/.../sessionmapcompare/` |
| Wiring | `SessionComparisonScreen` |

## Tests

- Automated: yes — `SessionMapCompareTest`
- Coverage: drop no-fix samples

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
