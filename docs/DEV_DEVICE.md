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

## Local Android gates

Set `JAVA_HOME` to your JDK 17 install before running `python3 scripts/agent-run.py feature-gate --stack android` locally. When `JAVA_HOME` is unset, `feature-gate.sh --stack multi` skips the Android lane — **CI remains canonical** for compile and unit tests.