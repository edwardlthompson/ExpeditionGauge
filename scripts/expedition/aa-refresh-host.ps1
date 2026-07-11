# Refresh Android Auto host discovery after ExpeditionGauge install/upgrade.
# Clears stale CarAppService category cache (e.g. IOT → POI).
param(
    [string]$Serial = "",
    [switch]$Clear,
    [string]$Apk = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig

if (-not $Serial) {
    $Serial = Get-AdbSerial -Config $config
}
if (-not $Serial) {
    Write-Error "aa-refresh-host: no ADB serial (pass -Serial or set project.config.json devDevice.adbSerial)"
}

function Invoke-Adb {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Args)
    & adb -s $Serial @Args
    if ($LASTEXITCODE -ne 0) { throw "adb failed: adb -s $Serial $($Args -join ' ')" }
}

$state = (adb devices | Select-String "^$Serial\s+device")
if (-not $state) {
    Write-Error "aa-refresh-host: device $Serial not connected"
}

$pkg = "dev.foss.expeditiongauge"
$aaPkg = "com.google.android.projection.gearhead"

if ($Apk) {
    if (-not (Test-Path $Apk)) { Write-Error "aa-refresh-host: APK not found: $Apk" }
    Write-Host "Installing $Apk ..." -ForegroundColor Cyan
    Invoke-Adb install -r $Apk
}

Write-Host "Force-stopping Android Auto ($aaPkg) ..." -ForegroundColor Cyan
Invoke-Adb shell am force-stop $aaPkg
Invoke-Adb shell am force-stop $pkg

if ($Clear) {
    Write-Host "Clearing Android Auto app data (re-enable developer mode + Unknown sources after this) ..." -ForegroundColor Yellow
    Invoke-Adb shell pm clear $aaPkg
}

$dump = (Invoke-Adb shell dumpsys package $pkg) -join "`n"
if ($dump -notmatch "ExpeditionGaugeCarAppService") {
    Write-Error "aa-refresh-host: ExpeditionGaugeCarAppService not registered — install ExpeditionGauge first"
}
if ($dump -notmatch "androidx\.car\.app\.category\.POI") {
    Write-Error "aa-refresh-host: category.POI missing (still IOT or old APK?) — install >= 2.14.1"
}
if ($dump -match "androidx\.car\.app\.category\.IOT") {
    Write-Warning "aa-refresh-host: dumpsys still mentions IOT — confirm you installed the POI build"
}

Write-Host @"

OK  Package shows ExpeditionGaugeCarAppService + category.POI

Next (on phone):
  1. Open Android Auto → unlock developer mode if cleared → Unknown sources ON
  2. Customize launcher → enable ExpeditionGauge
  3. Launch ExpeditionGauge once on the phone
  4. USB reconnect to the head unit (prefer USB for first discover)

If still missing after that: see Escalation in docs/help/ANDROID_AUTO.md — do NOT change categories again.
"@ -ForegroundColor Green
