# Feature: drift-run-ranking

> Library ranks sessions by drift score (max β + 2× slip events), after favorites.

## Acceptance criteria

- ✅ Score = maxBeta + 2 * slipEventCount
- ✅ Favorites still sort first
- ✅ Higher score ranks higher among non-favorites
- ✅ i18n: none (sort only)

## Smoke scenario

1. Given three sessions with different β and one starred
2. When the library opens
3. Then the star is first and the rest follow score

## Container map

| Layer | Path |
|-------|------|
| Logic | `driftrunranking/DriftRunRanking.kt` |
| Tests | `app/src/test/.../driftrunranking/` |
| Wiring | `SessionListScreen` |

## Tests

- Automated: yes — `DriftRunRankingTest`
- Coverage: score; favorite-then-score

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
