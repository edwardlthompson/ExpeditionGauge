# Feature: alert-history-log

> Keep the last 50 local alert edges even when not recording.

## Acceptance criteria

- ✅ Rising-edge alerts append to a local log (newest first)
- ✅ Cap at 50 entries
- ✅ Settings Alerts shows the latest 8 and a clear action
- ✅ i18n: `alerts_history_*`

## Smoke scenario

1. Given an over-limit edge fires
2. When Settings → Alerts is opened
3. Then the type and values appear at the top of the history list

## Container map

| Layer | Path |
|-------|------|
| Logic | `alerthistory/AlertHistory.kt` |
| Store | `settings/AlertHistoryStore.kt` |
| View | `ui/alerthistory/AlertHistoryField.kt` |
| Tests | `src/test/.../alerthistory/` |
| Wiring | AlertService edge append |

## Tests

- Automated: yes — `AlertHistoryTest`
- Coverage: prepend; cap; encode/decode

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
