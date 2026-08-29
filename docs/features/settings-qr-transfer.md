# Feature: settings-qr-transfer

> Frame a settings backup blob as a local `egset|v1|` QR payload.

## Acceptance criteria

- ✅ Payload starts with `egset|v1|`
- ✅ Foreign URLs parse as null
- ✅ Secret keys still dropped via `SettingsJsonBackup`
- ✅ i18n: `settings_qr_transfer`

## Smoke scenario

1. Given Advanced settings
2. When Settings QR is shown
3. Then the label starts with `egset|v1|` and has no adapter address

## Container map

| Layer | Path |
|-------|------|
| Logic | `settingsqrtransfer/SettingsQrTransfer.kt` |
| View | `ui/settingsqrtransfer/SettingsQrLabel.kt` |
| Tests | `app/src/test/.../settingsqrtransfer/` |
| Wiring | `SettingsAdvancedCategory` |

## Tests

- Automated: yes — `SettingsQrTransferTest`
- Coverage: frame/parse; reject foreign payload

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
