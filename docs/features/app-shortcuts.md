# Feature: app-shortcuts

> Launcher shortcuts for Record (dashboard) and Library (sessions).

## Acceptance criteria

- ✅ Shortcut IDs are `record` and `library`
- ✅ Library action opens Sessions
- ✅ Record action stays on the dashboard
- ✅ i18n: `shortcut_record`, `shortcut_library`

## Smoke scenario

1. Given a long-press on the launcher icon
2. When Library is chosen
3. Then Sessions opens

## Container map

| Layer | Path |
|-------|------|
| Logic | `appshortcuts/AppShortcuts.kt` |
| View | `ui/appshortcuts/AppShortcutEffect.kt` |
| Tests | `app/src/test/.../appshortcuts/` |
| Wiring | `MainActivity`, `AppScreenDashboardRoute`, `shortcuts.xml` |

## Tests

- Automated: yes — `AppShortcutsTest`
- Coverage: remember/consume; targets

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
