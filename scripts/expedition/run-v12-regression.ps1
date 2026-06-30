# v1.2.0 release regression matrix (Sprint 17b).
param(
    [string]$Serial = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial $config }

$scenarios = @(
    "drift-simulation",
    "crawling-mode",
    "polish-off-regression",
    "preset-switch-mid-drive",
    "playback-keyboard-seek",
    "playback-layout-rotation",
    "mark-event-export",
    "session-compare-drift",
    "talkback-labels"
)

$failed = @()
foreach ($scenario in $scenarios) {
    Write-Host "=== v12 regression: $scenario ===" -ForegroundColor Cyan
    & "$PSScriptRoot\adb-smoke.ps1" -Sprint 17 -Scenario $scenario -Serial $Serial
    if ($LASTEXITCODE -ne 0) {
        $failed += $scenario
    }
}

if ($failed.Count -gt 0) {
    Write-Host "v12 regression FAILED: $($failed -join ', ')" -ForegroundColor Red
    exit 1
}

Write-Host "v12 regression matrix: OK ($($scenarios.Count) scenarios)" -ForegroundColor Green
