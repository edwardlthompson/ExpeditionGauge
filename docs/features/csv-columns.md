# Feature: csv-columns

> Settings picker chooses which CSV columns export includes.

## Acceptance criteria

- ✅ Enabled columns persist in `CsvColumnStore`
- ✅ CSV export filters header and rows to those columns
- ✅ Empty selection falls back to all columns
- ✅ i18n: column ids are the header names

## Smoke scenario

1. Given only `timestampMs` and `speedMps` are selected
2. When a session is exported as CSV
3. Then the file has those two columns

## Container map

| Layer | Path |
|-------|------|
| Logic | `csvcolumns/CsvColumnPicker.kt` |
| Store | `settings/CsvColumnStore.kt` |
| View | `ui/csvcolumns/CsvColumnPickerField.kt` |
| Tests | `app/src/test/.../csvcolumns/` |
| Wiring | `ExportService` + `SettingsRecordingCategory` |

## Tests

- Automated: yes — `CsvColumnPickerTest`
- Coverage: filter; toggle; unknown columns dropped

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
