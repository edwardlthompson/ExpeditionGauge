#Requires -Version 5.1
<#
.SYNOPSIS
  Inject simulated OBD DTCs for AA ROW footer preview on DHU (debug APK).

.DESCRIPTION
  Starts head-unit server + DHU (optional), opens ExpeditionGauge, broadcasts
  two sample DTCs (P0420 + P0300 by default), then captures the DHU window.

.EXAMPLE
  pwsh scripts/expedition/dhu-sim-dtc.ps1 -RestartDhu
  pwsh scripts/expedition/dhu-sim-dtc.ps1 -Codes "P0420,P0301" -SkipDhu
#>
param(
    [string]$Serial = "",
    [string]$Codes = "P0420,P0300",
    [switch]$RestartDhu,
    [switch]$SkipDhu,
    [string]$OutFile = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial -Config $config }
if (-not $Serial) { Write-Error "dhu-sim-dtc: no ADB serial" }
if (-not $OutFile) {
    $OutFile = Join-Path $Root ".cursor\screenshots\dhu-dtc-sim.png"
}

$pkg = "dev.foss.expeditiongauge"
$action = "dev.foss.expeditiongauge.action.SIMULATE_DTC"
$receiver = "$pkg/.debug.SimDtcReceiver"

if (-not $SkipDhu) {
    Write-Host "== DHU smoke prelude ==" -ForegroundColor Cyan
    & "$PSScriptRoot\dhu-smoke.ps1" -Serial $Serial -RestartDhu:$RestartDhu -OutFile $OutFile
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "dhu-smoke exited $LASTEXITCODE — continuing with DTC inject"
    }
}

Write-Host "== Ensure app process ==" -ForegroundColor Cyan
& adb.exe -s $Serial shell monkey -p $pkg -c android.intent.category.LAUNCHER 1 | Out-Null
Start-Sleep -Seconds 2

Write-Host "== Simulate DTCs: $Codes ==" -ForegroundColor Cyan
& adb.exe -s $Serial shell am broadcast -a $action -n $receiver --es codes $Codes
if ($LASTEXITCODE -ne 0) { Write-Error "dhu-sim-dtc: broadcast failed" }

Write-Host "Waiting for AA footer invalidate (5s carousel) ..." -ForegroundColor DarkGray
Start-Sleep -Seconds 3

Write-Host "== Capture ==" -ForegroundColor Cyan
powershell.exe -NoProfile -File "$PSScriptRoot\capture-dhu-window.ps1" -OutFile $OutFile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$log = (& adb.exe -s $Serial logcat -d -t 40 ExpeditionGauge/SimDtc:I ExpeditionGauge/Obd:I *:S 2>&1) -join "`n"
Write-Host $log -ForegroundColor DarkGray
Write-Host "OK  $OutFile — expect bold-red '1/2  P0420 …' under ROW cubes (cycles every 5s)." -ForegroundColor Green
exit 0
