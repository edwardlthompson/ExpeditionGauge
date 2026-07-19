# Primary development device

| Field | Value |
|-------|-------|
| Model | OnePlus 13 (CPH2655) |
| Connection | USB ADB |
| Bootloader | Unlocked |
| Root | Yes (adb-root / Magisk as available) |
| ADB serial | `8bf09993` |

**Alternate:** OnePlus 12 (CPH2583) · serial `b5214fc6` — still valid; set `project.config.json` → `devDevice.adbSerial` when that phone is attached.

Detect serial: `adb devices -l`

Configure in `project.config.json` → `devDevice.adbSerial` when multiple devices are attached.

Smoke tests: `pwsh scripts/expedition/adb-smoke.ps1 -Sprint N -Scenario <name>`

AA / DHU: `pwsh scripts/expedition/dhu-smoke.ps1 -Serial <serial> -RestartDhu`

## Local Android gates

Set `JAVA_HOME` to your JDK 17 install before running `python3 scripts/agent-run.py feature-gate --stack android` locally. When `JAVA_HOME` is unset, `feature-gate.sh --stack multi` skips the Android lane — **CI remains canonical** for compile and unit tests.

**Windows + WSL:** `scripts/agent-run.py` prefers **Git Bash** (`C:\Program Files\Git\bin\bash.exe`) over WSL’s `System32\bash.exe`. WSL bash does not see Windows `JAVA_HOME`, so the Android gate exits `2` even when JDK 17 is installed. Install [Git for Windows](https://git-scm.com/download/win) if Git Bash is missing.
