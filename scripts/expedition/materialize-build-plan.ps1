# Materialize canonical ExpeditionGauge repo files from BUILD_PLAN template.
param(
    [string]$SourcePlan = "",
    [switch]$Force
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
Set-Location $Root

$defaultPlan = Join-Path $Root "BUILD_PLAN.md"
if ($SourcePlan -and (Test-Path $SourcePlan)) {
    $raw = Get-Content $SourcePlan -Raw
    if ($raw -match '(?s)^---\r?\n.*?\r?\n---\r?\n') {
        $body = $raw -replace '(?s)^---\r?\n.*?\r?\n---\r?\n', ''
    } else {
        $body = $raw
    }
    Write-Utf8NoBom $defaultPlan $body.TrimStart()
    Write-Host "Wrote BUILD_PLAN.md from $SourcePlan"
} elseif (-not (Test-Path $defaultPlan)) {
    throw "BUILD_PLAN.md missing and no -SourcePlan provided"
}

$adbSerial = ""
try {
    $line = & adb devices 2>$null | Select-Object -Skip 1 | Where-Object { $_ -match '\tdevice' } | Select-Object -First 1
    if ($line) { $adbSerial = ($line -split '\t')[0].Trim() }
} catch { }

$config = @{
    projectName = "ExpeditionGauge"
    purpose = "Offline-first automotive HUD with recording and playback"
    stack = "android"
    maintainer = "edward"
    releaseRepo = "github.com/OWNER/ExpeditionGauge"
    donationsUrl = ""
    releaseDraft = $true
    sprints = @{
        core_v1 = $true
        wave1_polish = $true
        wave2_polish = $true
        v2_video = $true
        v2_live_telemetry = $true
    }
    features = @{
        liveTelemetryEnabled = $false
        tpmsEnabled = $false
        externalGpsEnabled = $false
    }
    devDevice = @{
        model = "OnePlus 12"
        connection = "usb-adb"
        bootloaderUnlocked = $true
        rooted = $true
        adbSerial = $adbSerial
    }
}
$configJson = ($config | ConvertTo-Json -Depth 6)
Write-Utf8NoBom (Join-Path $Root "project.config.json") $configJson
Write-Utf8NoBom (Join-Path $Root "project.config.json.example") $configJson

$startHere = @'
# ExpeditionGauge — Agent Start Here

> Read this file first on every Cursor session. Canonical plan lives in git — never edit `.cursor/plans/`.

## Read order

1. [`BUILD_PLAN.md`](../BUILD_PLAN.md) — architecture + sprint board
2. [`project.config.json`](../project.config.json) — wave toggles, dev device, feature flags
3. [`docs/DEV_DEVICE.md`](DEV_DEVICE.md) — primary ADB hardware
4. [`docs/RECOMMENDATIONS.md`](RECOMMENDATIONS.md) — accepted stakeholder features
5. Active stack only: [`modules/android/MODULE.md`](../modules/android/MODULE.md), [`examples/android/`](../examples/android/)

## Resume workflow

```powershell
pwsh scripts/expedition/resume-agent.ps1
```

Execute the next `🔲 [AGENT]` row in BUILD_PLAN.md. After each step:

```powershell
pwsh scripts/watch-agent-gates.ps1
```

Mark completed rows via sprint sign-off or `mark-task.ps1`.

## Blockers {#blockers}

These are **not** BUILD_PLAN tasks. Scripts exit `2`; agent halts until resolved.

1. **GitHub credentials** — run the command printed by `ensure-gh-auth.ps1`, then re-run `bootstrap.ps1`.
2. **ADB device absent** — `[ADB]` rows need hardware. Run `pwsh scripts/expedition/adb-wait-device.ps1`. Primary dev device: OnePlus 12 (USB ADB, unlocked, rooted).
3. **Product judgment** — user edits `project.config.json` in chat → AGENT commits (one file only).

## Sprint 0 bootstrap (once)

```powershell
pwsh scripts/expedition/materialize-build-plan.ps1
pwsh scripts/expedition/bootstrap.ps1 -Init
pwsh scripts/expedition/sync-project-config.ps1
pwsh scripts/expedition/sprint-signoff.ps1 -Sprint 0
```
'@
Write-Utf8NoBom (Join-Path $Root "docs\START_HERE.md") $startHere

$devDevice = @"
# Primary development device

| Field | Value |
|-------|-------|
| Model | OnePlus 12 (CPH2583) |
| Connection | USB ADB |
| Bootloader | Unlocked |
| Root | Yes |
| ADB serial | ``$adbSerial`` |

Detect serial: ``adb devices -l``

Configure in ``project.config.json`` → ``devDevice.adbSerial`` when multiple devices are attached.

Smoke tests: ``pwsh scripts/expedition/adb-smoke.ps1 -Sprint N -Scenario <name>``
"@
Write-Utf8NoBom (Join-Path $Root "docs\DEV_DEVICE.md") $devDevice

$recPath = Join-Path $Root "docs\RECOMMENDATIONS.md"
if (-not (Test-Path $recPath) -or $Force) {
    $rec = @'
# Accepted stakeholder recommendations

Canonical copy of the accepted recommendation set from BUILD_PLAN.md. Every item maps to a sprint in the traceability matrix — no orphan features.

See BUILD_PLAN.md sections **Stakeholder recommendations (accepted — in scope)** and **Recommendations traceability matrix** for full tables.

## Summary

| # | Recommendation | Sprint |
|---|----------------|--------|
| 1 | Event marking + telemetry snapshot | 6 stub / 17 |
| 2 | Session tags + filter | 9 |
| 3 | Predictive / theoretical best timing | 10 |
| 4 | G / slip heatmap on map | 11 |
| 5 | Vehicle outline + drift in playback | 7 |
| 6 | Telemetry graph panel | 11 |
| 7 | Low-speed crawl mode | 9 |
| 8 | Session comparison basics | 17 |
| 9 | HTML share summary export | 17 |
| 10 | One-tap Go Live + QR | 19 |
| 11 | Driver-first recording UI | 6 |
| 12 | Preset modes (Drift/Offroad/Track/Minimal) | 15 |
| 13 | Calibration wizard + test drive | 17 / 18 |
| 14 | Playback session cards | 7 / 17 |
| 15 | Dark theme + Bright Day Mode | 1–2 |
| 16 | Audio feedback (tones + TTS) | 13 / 17 |
| 17 | Multi-IMU status indicators | 4 |
| 18 | Onboarding tour | 17 |
| 19 | Modular telemetry pipeline | 3 |
| 20 | Phone-only fusion fallback | 3–4 |
| 21 | Performance / thermal budget | 3, 8 |
| 22 | Flexible data model | 6 |
| 23 | Real-device ADB testing | 3–14, 19 |
| 24 | Extend new sensor types | 8 |
| 25 | Developer / Advanced mode | 18 |
| 26 | Vehicle profiles | 15 |
| 27 | Video sync + overlay | 18 |
| 28 | External Bluetooth GPS (NMEA) | 5c |

Also in plan: BLE TPMS (5b), Attitude G-meter (2–3), Live Telemetry (19).
'@
    Write-Utf8NoBom $recPath $rec
}

$rule = @'
---
description: ExpeditionGauge canonical BUILD_PLAN and project config
alwaysApply: true
---

# ExpeditionGauge plan rule

On session start:

1. Read `docs/START_HERE.md`, `BUILD_PLAN.md`, and `project.config.json`.
2. Run `pwsh scripts/expedition/resume-agent.ps1` to find the next `🔲 [AGENT]` task.
3. **Never edit** `.cursor/plans/*.plan.md` — git-tracked `BUILD_PLAN.md` is canonical.
4. Respect `project.config.json` sprint wave toggles; skip disabled polish/v2 sections.
5. `[ADB]` tasks require physical device; use `adb-wait-device.ps1` when absent (blocker, not a plan row).
6. Package namespace: `dev.foss.expeditiongauge`.
'@
Write-Utf8NoBom (Join-Path $Root ".cursor\rules\expeditiongauge-plan.mdc") $rule

Write-Host "Materialized canonical ExpeditionGauge files." -ForegroundColor Green
