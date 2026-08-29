# Feature: privacy-report-export

> Share a sanitized privacy report from Settings → Advanced.

## Acceptance criteria

- ✅ Export markdown runs through `SanitizeReport`
- ✅ Tokens, emails, and home paths are redacted
- ✅ Settings Advanced has an export action
- ✅ i18n: `privacy_report_export`

## Smoke scenario

1. Given Advanced settings
2. When Export privacy report is tapped
3. Then the share sheet gets a redacted markdown body

## Container map

| Layer | Path |
|-------|------|
| Logic | `privacyreportexport/PrivacyReportExport.kt` |
| View | `ui/privacyreportexport/PrivacyReportExportButton.kt` |
| Tests | `app/src/test/.../privacyreportexport/` |
| Wiring | `SettingsAdvancedCategory` |

## Tests

- Automated: yes — `PrivacyReportExportTest`
- Coverage: secret redaction; share extra

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
