# Refresh Android Auto host discovery after ExpeditionGauge install/upgrade.
#
# Critical: plain `adb install` / `pm install -i com.android.vending` leaves
# initiatingPackageName=com.android.shell. Android Auto Customize launcher hides
# those apps (Car Scanner works because initiator=com.android.vending).
# This script creates the install session as the Play Store UID so both
# installer and initiator are com.android.vending.
param(
    [string]$Serial = "",
    [switch]$Clear,
    [string]$Apk = "",
    [switch]$NoPlayStoreInstaller
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
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$AdbArgs)
    & adb -s $Serial @AdbArgs
    if ($LASTEXITCODE -ne 0) { throw "adb failed: adb -s $Serial $($AdbArgs -join ' ')" }
}

$state = (adb devices | Select-String "^$Serial\s+device")
if (-not $state) {
    Write-Error "aa-refresh-host: device $Serial not connected"
}

$pkg = "dev.foss.expeditiongauge"
$aaPkg = "com.google.android.projection.gearhead"
$playInstaller = "com.android.vending"

function Install-AsPlayStore([string]$ApkPath) {
    Write-Host "Root + install session as Play Store UID (installer+initiator=$playInstaller) ..." -ForegroundColor Cyan
    Invoke-Adb root | Out-Null
    Start-Sleep -Seconds 1
    $apkFull = (Resolve-Path $ApkPath).Path
    $remote = "/data/local/tmp/ExpeditionGauge-aa-install.apk"
    Invoke-Adb push $apkFull $remote | Out-Null

    # Android 14+ dumpsys often prints appId=; older builds used userId=.
    $vendingUid = (adb -s $Serial shell "cmd package list packages -U $playInstaller" |
        Select-String -Pattern "uid:(\d+)" |
        Select-Object -First 1)
    if (-not $vendingUid) {
        $vendingUid = (adb -s $Serial shell "dumpsys package $playInstaller" |
            Select-String -Pattern "(?:userId|appId)=(\d+)" |
            Select-Object -First 1)
    }
    if (-not $vendingUid) { throw "aa-refresh-host: could not resolve $playInstaller uid" }
    $uid = $vendingUid.Matches[0].Groups[1].Value
    Write-Host "Play Store uid=$uid" -ForegroundColor DarkGray

    $size = (adb -s $Serial shell "stat -c %s $remote").ToString().Trim()
    $createCmd = "pm install-create --user 0 -i $playInstaller -r -S $size"
    $create = (adb -s $Serial shell "su $uid -c `"$createCmd`"" 2>&1 | Out-String)
    if ($create -match "su: inaccessible|not found|No such file") {
        $helperLocal = Join-Path $PSScriptRoot "bin\run-as-uid-arm64"
        if (-not (Test-Path $helperLocal)) {
            throw "aa-refresh-host: Magisk su missing and no run-as-uid-arm64 helper at $helperLocal"
        }
        Write-Host "Magisk su missing — using run-as-uid-arm64 helper (adb-root devices)" -ForegroundColor Yellow
        $helperRemote = "/data/local/tmp/run-as-uid"
        Invoke-Adb push $helperLocal $helperRemote | Out-Null
        Invoke-Adb shell "chmod 755 $helperRemote" | Out-Null
        $create = (adb -s $Serial shell "$helperRemote $uid $createCmd").ToString()
    }
    if ($create -notmatch "\[(\d+)\]") {
        throw "aa-refresh-host: install-create failed: $create"
    }
    $sid = $Matches[1]
    # Session created as vending; write/commit as shell (allowed) — initiator stays vending.
    Invoke-Adb shell "pm install-write -S $size $sid base $remote" | Out-Null
    Invoke-Adb shell "pm install-commit $sid" | Out-Null
    Invoke-Adb shell "rm -f $remote" | Out-Null
}

if ($Apk) {
    if (-not (Test-Path $Apk)) { Write-Error "aa-refresh-host: APK not found: $Apk" }
    if ($NoPlayStoreInstaller) {
        Write-Host "Installing via adb install -r (Customize launcher likely HIDDEN) ..." -ForegroundColor Yellow
        Invoke-Adb install -r ((Resolve-Path $Apk).Path)
    }
    else {
        Install-AsPlayStore -ApkPath $Apk
    }
}

Write-Host "Force-stopping Android Auto ($aaPkg) ..." -ForegroundColor Cyan
Invoke-Adb shell am force-stop $aaPkg
Invoke-Adb shell am start -n "$pkg/.MainActivity" | Out-Null

if ($Clear) {
    Write-Host "Clearing Android Auto app data (re-enable developer mode + Unknown sources after this) ..." -ForegroundColor Yellow
    Invoke-Adb shell pm clear $aaPkg
}

$dump = (Invoke-Adb shell dumpsys package $pkg) -join "`n"
if ($dump -notmatch "ExpeditionGaugeCarAppService") {
    Write-Error "aa-refresh-host: ExpeditionGaugeCarAppService not registered — install ExpeditionGauge first"
}
if ($dump -notmatch "androidx\.car\.app\.category\.POI") {
    Write-Error "aa-refresh-host: category.POI missing — install >= 2.14.1"
}
if ($dump -notmatch "installerPackageName=$playInstaller") {
    Write-Warning "aa-refresh-host: installerPackageName is not $playInstaller"
}
if ($dump -notmatch "initiatingPackageName=$playInstaller") {
    Write-Warning "aa-refresh-host: initiatingPackageName is not $playInstaller — Customize launcher will stay empty on modern AA"
}

Write-Host @"

OK  Package shows CarAppService + category.POI
    Expect installerPackageName=$playInstaller AND initiatingPackageName=$playInstaller

Next (on phone):
  1. Android Auto → Unknown sources ON
  2. Customize launcher → enable ExpeditionGauge (should be listed)
  3. USB reconnect to the head unit

Smoke: pwsh scripts/expedition/aa-smoke-customize-launcher.ps1 -SkipReinstall -SkipPhenotype
"@ -ForegroundColor Green
