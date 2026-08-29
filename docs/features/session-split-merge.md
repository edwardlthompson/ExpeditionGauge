# Feature: session-split-merge

> Split a session at a timestamp or merge two sample lists into one session.

## Acceptance criteria

- ✅ Split keeps samples `< atMs` in part 1 and `>= atMs` in part 2
- ✅ Merge sorts by timestamp and remaps `sessionId`
- ✅ Edit screen can split at the sample midpoint
- ✅ i18n: `session_split_midpoint`

## Smoke scenario

1. Given a session with samples at 1s, 2s, 3s
2. When Split at midpoint runs
3. Then two sessions exist and the original is removed

## Container map

| Layer | Path |
|-------|------|
| Logic | `sessionsplitmerge/SessionSplitMerge.kt` |
| Repo | `sessionsplitmerge/SessionSplitMergeRepo.kt` |
| View | `ui/sessionsplitmerge/SessionSplitMergeButtons.kt` |
| Tests | `app/src/test/.../sessionsplitmerge/` |
| Wiring | `SessionMetadataEditScreen` |

## Tests

- Automated: yes — `SessionSplitMergeTest`
- Coverage: split bounds; merge sort; remap ids

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
