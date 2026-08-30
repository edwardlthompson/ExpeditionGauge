# Smoke: head-unit server + DHU + open ExpeditionGauge + capture dhu-live.png
#
# Usage:
#   pwsh scripts/expedition/dhu-smoke.ps1
#   pwsh scripts/expedition/dhu-smoke.ps1 -Serial b5214fc6 -RestartDhu
#   pwsh scripts/expedition/dhu-smoke.ps1 -Tall -RestartDhu
#     → uses examples/android/car/config/dhu-tall.ini (480x800), captures dhu-vertical-2cube.png,
#       restores previous %USERPROFILE%\.android\headunit.ini after capture
param(
    [string]$Serial = "",
    [string]$AdbPort = "5277",
    [switch]$RestartDhu,
    [switch]$Tall,
    [string]$OutFile = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial -Config $config }
if (-not $Serial) { Write-Error "dhu-smoke: no ADB serial" }
if (-not $OutFile) {
    if ($Tall) {
        $OutFile = Join-Path $Root ".cursor\screenshots\dhu-vertical-2cube.png"
    } else {
        $OutFile = Join-Path $Root ".cursor\screenshots\dhu-live.png"
    }
}

$androidDir = Join-Path $env:USERPROFILE ".android"
$headunitIni = Join-Path $androidDir "headunit.ini"
$headunitBackup = Join-Path $androidDir "headunit.ini.eg-smoke-bak"
$tallIni = Join-Path $Root "examples\android\car\config\dhu-tall.ini"
$dhuConfigArg = @()

function Restore-HeadunitIni {
    if (Test-Path $headunitBackup) {
        Move-Item -Force $headunitBackup $headunitIni
        Write-Host "Restored $headunitIni from backup" -ForegroundColor DarkGray
    }
}

if ($Tall) {
    if (-not (Test-Path $tallIni)) {
        Write-Error "dhu-smoke: missing tall config $tallIni"
    }
    if (-not (Test-Path $androidDir)) {
        New-Item -ItemType Directory -Path $androidDir | Out-Null
    }
    if (Test-Path $headunitIni) {
        Copy-Item -Force $headunitIni $headunitBackup
    } elseif (Test-Path $headunitBackup) {
        Remove-Item -Force $headunitBackup
    }
    Copy-Item -Force $tallIni $headunitIni
    $dhuConfigArg = @("-c", $headunitIni)
    Write-Host "Tall mode: $headunitIni (480x800)" -ForegroundColor Cyan
}

try {
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
    if ($RestartDhu -or $Tall) {
        if ($existing) {
            $existing | Stop-Process -Force
            Start-Sleep -Seconds 1
            $existing = $null
        }
    }
    if (-not $existing -or $RestartDhu -or $Tall) {
        # Controlled start: named pipe → DHU stdin so open uses console `tap` (no mouse).
        & "$PSScriptRoot\dhu-start-controlled.ps1" -AdbPort $AdbPort -RestartDhu -DhuExtraArgs $dhuConfigArg
        if ($LASTEXITCODE -ne 0) {
            Write-Warning "dhu-smoke: controlled start failed — bare Start-Process fallback"
            $args = @("-a", $AdbPort, "-i", "touch") + $dhuConfigArg
            Start-Process -FilePath $dhu -ArgumentList $args -WorkingDirectory (Split-Path $dhu)
        }
        Write-Host "Started DHU — waiting for projection ..." -ForegroundColor DarkGray
        Start-Sleep -Seconds 8
    } else {
        Start-Sleep -Seconds 2
    }

    Write-Host "== 3) Open ExpeditionGauge on DHU ==" -ForegroundColor Cyan
    # pwsh: _expedition-common.ps1 is UTF-8 (emoji regex). powershell.exe 5.1 mis-parses it.
    pwsh -NoProfile -File "$PSScriptRoot\dhu-open-expeditiongauge.ps1" -MouseFallback
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "dhu-smoke: open-app helper failed (exit $LASTEXITCODE) — capture anyway"
    }

    Write-Host "== 4) Capture ==" -ForegroundColor Cyan
    powershell.exe -NoProfile -File "$PSScriptRoot\capture-dhu-window.ps1" -OutFile $OutFile
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

    $bytes = (Get-Item $OutFile).Length
    if ($bytes -lt 40000) {
        Write-Error "dhu-smoke: capture too small ($bytes bytes) — still waiting for phone or blank?"
    }

    $log = (& adb.exe -s $Serial logcat -d -t 80 DrivePaneScreen:E DriveMapHudScreen:E *:S 2>&1) -join "`n"
    if ($log -match "onGetTemplate failed|NavigationTemplate failed") {
        Write-Warning "dhu-smoke: template failure in logcat — see below"
        Write-Host $log -ForegroundColor Yellow
        exit 1
    }

    Write-Host "OK  $OutFile ($bytes bytes)" -ForegroundColor Green
    if ($Tall) {
        Write-Host "Tall smoke: confirm 1x2 Attitude|Telemetry and no Mute/Capture/Record/Level chrome." -ForegroundColor Yellow
    }
    exit 0
} finally {
    if ($Tall) {
        Restore-HeadunitIni
    }
}
