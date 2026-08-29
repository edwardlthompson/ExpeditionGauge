# Feature: last-session-widget

> Home-screen widget showing the last recording name.

## Acceptance criteria

- ✅ Blank name shows `No session`
- ✅ Library list writes the newest session into the widget store
- ✅ Tap opens the session library shortcut
- ✅ i18n: `last_session_empty`

## Smoke scenario

1. Given a recorded session named Night run
2. When the widget updates
3. Then it shows Night run

## Container map

| Layer | Path |
|-------|------|
| Logic | `lastsessionwidget/` |
| Tests | `app/src/test/.../lastsessionwidget/` |
| Wiring | `SessionListScreen`, `AndroidManifest` |

## Tests

- Automated: yes — `LastSessionLabelTest`
- Coverage: empty vs named

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
