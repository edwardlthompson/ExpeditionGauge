# Feature: storage-autodelete

> Delete oldest unprotected sessions when used bytes meet the storage cap.

## Acceptance criteria

- ✅ `needsPrune` when used ≥ allowed (allowed > 0)
- ✅ Oldest unprotected session may be deleted only while over cap
- ✅ Protected sessions are never auto-deleted (`SessionStorageBudget`)
- ✅ i18n: none (policy only)

## Smoke scenario

1. Given used storage meets the allowed cap
2. When a new session starts
3. Then oldest unprotected sessions are pruned first

## Container map

| Layer | Path |
|-------|------|
| Logic | `storageautodelete/StorageAutoDelete.kt` |
| Existing | `recording/SessionStorageBudget.kt` |
| Tests | `app/src/test/.../storageautodelete/` |

## Tests

- Automated: yes — `StorageAutoDeleteTest`
- Coverage: prune threshold; protected skip

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
