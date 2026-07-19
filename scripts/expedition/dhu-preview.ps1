# Launch Google Desktop Head Unit (DHU) from the CLI — no Android Studio UI required.
#
# Prerequisites (one-time):
#   1. Android SDK installed (ANDROID_HOME or ANDROID_SDK_ROOT)
#   2. SDK package: "Android Auto Desktop Head Unit Emulator"
#        sdkmanager "extras;google;auto"
#      or Android Studio → SDK Manager → SDK Tools → Android Auto Desktop Head Unit Emulator
#   3. Phone/emulator with ExpeditionGauge + Android Auto developer mode / Unknown sources
#
# Usage:
#   pwsh scripts/expedition/dhu-preview.ps1
#   pwsh scripts/expedition/dhu-preview.ps1 -Serial b5214fc6 -InstallApk .\ExpeditionGauge-debug.apk
#   pwsh scripts/expedition/dhu-preview.ps1 -ForwardOnly
#
# Day-to-day loop after editing :car in Cursor:
#   1. Build/install APK (or -InstallApk here)
#   2. pwsh scripts/expedition/aa-refresh-host.ps1 -Apk <apk>   # when discovery stale
#   3. Re-run this script (or leave DHU open) → reopen ExpeditionGauge on DHU
param(
    [string]$Serial = "",
    [string]$InstallApk = "",
    [string]$AdbPort = "5277",
    [ValidateSet("touch", "rotary", "hybrid")]
    [string]$InputMode = "touch",
    [switch]$ForwardOnly,
    [switch]$Headless
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig

if (-not $Serial) {
    $Serial = Get-AdbSerial -Config $config
}
if (-not $Serial) {
    Write-Error "dhu-preview: no ADB serial (pass -Serial or set project.config.json devDevice.adbSerial)"
}

function Resolve-AndroidSdk {
    foreach ($candidate in @($env:ANDROID_SDK_ROOT, $env:ANDROID_HOME)) {
        if ($candidate -and (Test-Path $candidate)) { return $candidate }
    }
    $local = Join-Path $env:LOCALAPPDATA "Android\Sdk"
    if (Test-Path $local) { return $local }
    return $null
}

function Resolve-DhuBinary([string]$Sdk) {
    $base = Join-Path $Sdk "extras\google\auto"
    $candidates = @(
        (Join-Path $base "desktop-head-unit.exe"),
        (Join-Path $base "desktop-head-unit"),
        (Join-Path $base "desktop_head_unit.exe"),
        (Join-Path $base "desktop_head_unit")
    )
    foreach ($c in $candidates) {
        if (Test-Path $c) { return $c }
    }
    return $null
}

$sdk = Resolve-AndroidSdk
if (-not $sdk) {
    Write-Error @"
dhu-preview: Android SDK not found. Set ANDROID_SDK_ROOT (or ANDROID_HOME), then install DHU:
  sdkmanager `"extras;google;auto`"
"@
}

$dhu = Resolve-DhuBinary $sdk
if (-not $dhu) {
    Write-Error @"
dhu-preview: Desktop Head Unit binary missing under $sdk\extras\google\auto\
Install once (no Android Studio UI needed after this):
  sdkmanager `"extras;google;auto`"
Or: Android Studio → Settings → Languages & Frameworks → Android SDK → SDK Tools →
    Android Auto Desktop Head Unit Emulator
"@
}

$state = (adb devices | Select-String "^$Serial\s+device")
if (-not $state) {
    Write-Error "dhu-preview: device $Serial not connected"
}

if ($InstallApk) {
    if (-not (Test-Path $InstallApk)) {
        Write-Error "dhu-preview: APK not found: $InstallApk"
    }
    Write-Host "Installing $InstallApk via aa-refresh-host.ps1 ..." -ForegroundColor Cyan
    & "$PSScriptRoot\aa-refresh-host.ps1" -Serial $Serial -Apk $InstallApk
    if ($LASTEXITCODE -ne 0) { throw "dhu-preview: aa-refresh-host failed" }
}

Write-Host "ADB forward tcp:$AdbPort → device $Serial ..." -ForegroundColor Cyan
& adb -s $Serial forward --remove "tcp:$AdbPort" 2>$null | Out-Null
& adb -s $Serial forward "tcp:$AdbPort" "tcp:$AdbPort"
if ($LASTEXITCODE -ne 0) {
    # Some AA builds use localabstract transport; try classic DHU forward pair.
    & adb -s $Serial forward "tcp:$AdbPort" "localabstract:android_auto_phone"
}

Write-Host @"

DHU ready.
  Binary: $dhu
  Device: $Serial
  Port:   $AdbPort
  Input:  $InputMode

On the phone: unlock → open Android Auto (or start projection) → launch ExpeditionGauge.
After code changes: rebuild/install, optionally aa-refresh-host.ps1, reopen app on DHU.
Bitmap-only review without DHU: pwsh scripts/expedition/aa-bitmap-preview.ps1

"@ -ForegroundColor Cyan

if ($ForwardOnly) {
    Write-Host "ForwardOnly — start DHU yourself: `"$dhu`" -a $AdbPort -i $InputMode" -ForegroundColor Yellow
    exit 0
}

$dhuArgs = @("-a", $AdbPort, "-i", $InputMode)
if ($Headless) { $dhuArgs += "-h" }

# Detached Start-Process: piping/redirecting stdin makes DHU exit immediately after connect.
Write-Host "Starting DHU window (close the DHU window to stop) ..." -ForegroundColor Green
$proc = Start-Process -FilePath $dhu -ArgumentList $dhuArgs -WorkingDirectory (Split-Path $dhu) -PassThru
Write-Host "DHU PID=$($proc.Id) — look for window: Android Auto - Desktop Head Unit" -ForegroundColor Green
exit 0
