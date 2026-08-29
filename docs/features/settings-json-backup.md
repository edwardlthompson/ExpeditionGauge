# Feature: settings-json-backup

> Pipe-encoded settings snapshot (no org.json). Device addresses stay out of the blob.

## Acceptance criteria

- ✅ Allowed keys round-trip through `|` / `=` escaping
- ✅ `obd_device` and other secrets are dropped
- ✅ Dedicated `SettingsBackupStore` holds the last blob
- ✅ i18n: `settings_json_backup`

## Smoke scenario

1. Given Advanced settings
2. When Backup settings is tapped
3. Then a pipe snapshot is stored without adapter addresses

## Container map

| Layer | Path |
|-------|------|
| Logic | `settingsjsonbackup/` |
| View | `ui/settingsjsonbackup/SettingsBackupButton.kt` |
| Tests | `app/src/test/.../settingsjsonbackup/` |
| Wiring | `SettingsAdvancedCategory` |

## Tests

- Automated: yes — `SettingsJsonBackupTest`
- Coverage: escape round-trip; secret drop

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
