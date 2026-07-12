# Install ExpeditionGauge for Android Auto (PC + rooted phone).
# Sets installerPackageName AND initiatingPackageName to com.android.vending.
#
# Usage:
#   pwsh install-aa-from-pc.ps1 -Apk .\ExpeditionGauge-2.14.1.apk
#   pwsh install-aa-from-pc.ps1 -Apk .\ExpeditionGauge-2.14.1.apk -Serial SERIAL
#   pwsh install-aa-from-pc.ps1 -DownloadLatest   # needs network + gh or curl
param(
    [string]$Apk = "",
    [string]$Serial = "",
    [switch]$DownloadLatest,
    [string]$Repo = "edwardlthompson/ExpeditionGauge"
)

$ErrorActionPreference = "Stop"
$play = "com.android.vending"
$pkg = "dev.foss.expeditiongauge"

function Adb {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$Args)
    if ($Serial) { & adb -s $Serial @Args } else { & adb @Args }
    if ($LASTEXITCODE -ne 0) { throw "adb failed: $($Args -join ' ')" }
}

if (-not (Get-Command adb -ErrorAction SilentlyContinue)) {
    Write-Error "adb not found. Install Android platform-tools and add to PATH."
}

$devices = adb devices | Select-String "`tdevice$"
if (-not $devices) { Write-Error "No adb device connected." }
if (-not $Serial -and (@($devices).Count -gt 1)) {
    Write-Error "Multiple devices; pass -Serial <id>"
}

if ($DownloadLatest -and -not $Apk) {
    Write-Host "Downloading latest ExpeditionGauge-*.apk from GitHub Releases..." -ForegroundColor Cyan
    $Apk = Join-Path $PWD "ExpeditionGauge-download.apk"
    if (Get-Command gh -ErrorAction SilentlyContinue) {
        gh release download -R $Repo -p "ExpeditionGauge-*.apk" -O $Apk --clobber
    }
    else {
        $api = Invoke-RestMethod "https://api.github.com/repos/$Repo/releases/latest"
        $asset = $api.assets | Where-Object { $_.name -like "ExpeditionGauge-*.apk" } | Select-Object -First 1
        if (-not $asset) { Write-Error "No ExpeditionGauge-*.apk on latest release" }
        Invoke-WebRequest -Uri $asset.browser_download_url -OutFile $Apk
    }
}

if (-not $Apk) {
    $Apk = Get-ChildItem "ExpeditionGauge-*.apk" -ErrorAction SilentlyContinue |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}
if (-not $Apk -or -not (Test-Path $Apk)) {
    Write-Error "Pass -Apk path to ExpeditionGauge-*.apk (or -DownloadLatest)"
}

Write-Host "Using APK: $Apk" -ForegroundColor Cyan
Adb root | Out-Null
Start-Sleep -Seconds 1
$id = (Adb shell id) -join ""
if ($id -notmatch "uid=0") {
    Write-Error @"
Device is not rooted (adb root failed).

Unrooted options:
  - Android <= 13: KingInstaller https://github.com/fcaronte/KingInstaller/releases
  - Android 14+: need Magisk/root for this script, or see docs/help/ANDROID_AUTO_SIDELOAD.md
"@
}

$remote = "/data/local/tmp/ExpeditionGauge-aa-install.apk"
Adb push $Apk $remote | Out-Null

$uidLine = (Adb shell "cmd package list packages -U $play") -join "`n"
if ($uidLine -notmatch "uid:(\d+)") {
    $uidLine = (Adb shell "dumpsys package $play") -join "`n"
    if ($uidLine -notmatch "(?:userId|appId)=(\d+)") {
        Write-Error "Play Store ($play) not installed — required to spoof install initiator"
    }
}
$uid = $Matches[1]
$size = (Adb shell "stat -c %s $remote").ToString().Trim()

$createCmd = "pm install-create --user 0 -i $play -r -S $size"
$create = (Adb shell "su $uid -c `"$createCmd`"" 2>&1 | Out-String)
if ($create -match "su: inaccessible|not found|No such file") {
    $helperLocal = Join-Path $PSScriptRoot "bin\run-as-uid-arm64"
    if (-not (Test-Path $helperLocal)) {
        Write-Error "Magisk su missing and no run-as-uid-arm64 helper — install Magisk or rebuild scripts/expedition/bin/run-as-uid-arm64"
    }
    Write-Host "Magisk su missing — using run-as-uid-arm64 helper" -ForegroundColor Yellow
    $helperRemote = "/data/local/tmp/run-as-uid"
    Adb push $helperLocal $helperRemote | Out-Null
    Adb shell "chmod 755 $helperRemote" | Out-Null
    $create = (Adb shell "$helperRemote $uid $createCmd").ToString()
}
if ($create -notmatch "\[(\d+)\]") { Write-Error "install-create failed: $create" }
$sid = $Matches[1]
Write-Host "Session $sid as uid $uid ($play)" -ForegroundColor Cyan
Adb shell "pm install-write -S $size $sid base $remote" | Out-Null
Adb shell "pm install-commit $sid" | Out-Null
Adb shell "rm -f $remote" | Out-Null
Adb shell "am start -n $pkg/.MainActivity" | Out-Null

$dump = (Adb shell "dumpsys package $pkg") -join "`n"
$inst = if ($dump -match "installerPackageName=(\S+)") { $Matches[1] } else { "?" }
$init = if ($dump -match "initiatingPackageName=(\S+)") { $Matches[1] } else { "?" }
Write-Host "installerPackageName=$inst"
Write-Host "initiatingPackageName=$init"
if ($inst -ne $play -or $init -ne $play) {
    Write-Error "Install attribution incomplete — Customize launcher will stay empty"
}

Write-Host @"

OK — Play Store attribution set.

On the phone:
  1. Android Auto → tap version 10x → Developer settings → Unknown sources ON
  2. Customize launcher → enable ExpeditionGauge
  3. USB reconnect to the car

Guide: https://github.com/$Repo/blob/main/docs/help/ANDROID_AUTO_SIDELOAD.md
"@ -ForegroundColor Green
