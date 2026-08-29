# Feature: saf-folder-picker

> Persistable SAF tree picker for export folders.

## Acceptance criteria

- ✅ Tree intent uses persistable read/write flags
- ✅ `/tree/` URIs are recognized
- ✅ Settings Advanced has a picker action
- ✅ i18n: `saf_folder_picker`

## Smoke scenario

1. Given Advanced settings
2. When Choose export folder is tapped
3. Then the system DocumentsUI tree picker opens

## Container map

| Layer | Path |
|-------|------|
| Logic | `saffolderpicker/SafFolderPicker.kt` |
| View | `ui/saffolderpicker/SafFolderButton.kt` |
| Tests | `app/src/test/.../saffolderpicker/` |
| Wiring | `SettingsAdvancedCategory` |

## Tests

- Automated: yes — `SafFolderPickerTest`
- Coverage: tree URI; persist flags

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
