# Feature: settings-search

> Filter Settings hub categories by keyword without growing the settings form.

## Acceptance criteria

- ✅ Empty query lists all categories
- ✅ `privacy` matches Advanced
- ✅ `obd` matches Hardware
- ✅ i18n: `settings_search`

## Smoke scenario

1. Given Settings hub
2. When the user types privacy
3. Then Advanced is the only match

## Container map

| Layer | Path |
|-------|------|
| Logic | `settingssearch/SettingsSearch.kt` |
| View | `ui/settingssearch/SettingsSearchField.kt` |
| Tests | `app/src/test/.../settingssearch/` |
| Wiring | `SettingsHub` |

## Tests

- Automated: yes — `SettingsSearchTest`
- Coverage: empty + keyword match

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
