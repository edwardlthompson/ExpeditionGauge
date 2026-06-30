# Primary development device

| Field | Value |
|-------|-------|
| Model | OnePlus 12 (CPH2583) |
| Connection | USB ADB |
| Bootloader | Unlocked |
| Root | Yes |
| ADB serial | `b5214fc6` |

Detect serial: `adb devices -l`

Configure in `project.config.json` → `devDevice.adbSerial` when multiple devices are attached.

Smoke tests: `pwsh scripts/expedition/adb-smoke.ps1 -Sprint N -Scenario <name>`