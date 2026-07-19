# Smoke: head-unit server + DHU + open ExpeditionGauge + capture dhu-live.png
#
# Usage:
#   pwsh scripts/expedition/dhu-smoke.ps1
#   pwsh scripts/expedition/dhu-smoke.ps1 -Serial b5214fc6 -RestartDhu
param(
    [string]$Serial = "",
    [string]$AdbPort = "5277",
    [switch]$RestartDhu,
    [string]$OutFile = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial -Config $config }
if (-not $Serial) { Write-Error "dhu-smoke: no ADB serial" }
if (-not $OutFile) {
    $OutFile = Join-Path $Root ".cursor\screenshots\dhu-live.png"
}

Write-Host "== 1) Head unit server ==" -ForegroundColor Cyan
& "$PSScriptRoot\aa-start-head-unit-server.ps1" -Serial $Serial -Port ([int]$AdbPort)
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "== 2) ADB forward + DHU ==" -ForegroundColor Cyan
& adb.exe -s $Serial forward --remove "tcp:$AdbPort" 2>$null | Out-Null
& adb.exe -s $Serial forward "tcp:$AdbPort" "tcp:$AdbPort"
if ($LASTEXITCODE -ne 0) { throw "dhu-smoke: adb forward failed" }

$dhu = Join-Path $env:LOCALAPPDATA "Android\Sdk\extras\google\auto\desktop-head-unit.exe"
if (-not (Test-Path $dhu)) {
    Write-Error "dhu-smoke: DHU binary missing at $dhu (sdkmanager extras;google;auto)"
}

$existing = Get-Process -Name "desktop-head-unit" -ErrorAction SilentlyContinue
if ($RestartDhu -and $existing) {
    $existing | Stop-Process -Force
    Start-Sleep -Seconds 1
    $existing = $null
}
if (-not $existing) {
    Start-Process -FilePath $dhu -ArgumentList @("-a", $AdbPort, "-i", "touch") -WorkingDirectory (Split-Path $dhu)
    Write-Host "Started DHU — waiting for projection ..." -ForegroundColor DarkGray
    Start-Sleep -Seconds 8
} else {
    Start-Sleep -Seconds 2
}

Write-Host "== 3) Open ExpeditionGauge on DHU ==" -ForegroundColor Cyan
powershell.exe -NoProfile -File "$PSScriptRoot\dhu-open-expeditiongauge.ps1"
if ($LASTEXITCODE -ne 0) {
    Write-Warning "dhu-smoke: open-app helper failed (exit $LASTEXITCODE) — capture anyway"
}

Write-Host "== 4) Capture ==" -ForegroundColor Cyan
powershell.exe -NoProfile -File "$PSScriptRoot\capture-dhu-window.ps1" -OutFile $OutFile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$bytes = (Get-Item $OutFile).Length
# Heuristic: Waiting-for-phone / empty ~5–20KB; real AA chrome usually >80KB
if ($bytes -lt 40000) {
    Write-Error "dhu-smoke: capture too small ($bytes bytes) — still waiting for phone or blank?"
}

# Fail if our error waiting template text is present (best-effort PNG string scan is weak);
# check logcat for recent DrivePaneScreen failures instead.
$log = (& adb.exe -s $Serial logcat -d -t 80 DrivePaneScreen:E *:S 2>&1) -join "`n"
if ($log -match "onGetTemplate failed") {
    Write-Warning "dhu-smoke: DrivePaneScreen logged onGetTemplate failed — see logcat"
    Write-Host $log -ForegroundColor Yellow
    exit 1
}

Write-Host "OK  $OutFile ($bytes bytes)" -ForegroundColor Green
exit 0
