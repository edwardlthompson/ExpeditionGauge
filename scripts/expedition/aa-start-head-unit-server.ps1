# Start Android Auto "Developer head unit server" (port 5277) via ADB.
# Used after aa-refresh-host force-stops Gearhead so DHU can reconnect without a manual tap.
#
# Strategy:
#   1) If :5277 already listening → OK
#   2) start-foreground-service DeveloperHeadUnitNetworkService
#   3) UI Automator: open Developer settings → tap "Start head unit server"
#
# Usage:
#   pwsh scripts/expedition/aa-start-head-unit-server.ps1
#   pwsh scripts/expedition/aa-start-head-unit-server.ps1 -Serial b5214fc6 -WaitSec 25
param(
    [string]$Serial = "",
    [int]$Port = 5277,
    [int]$WaitSec = 25
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial -Config $config }
if (-not $Serial) { Write-Error "aa-start-head-unit-server: no ADB serial" }

$aa = "com.google.android.projection.gearhead"
$svc = "$aa/.companion.DeveloperHeadUnitNetworkService"
$devSettings = "$aa/.companion.devsettings.DeveloperSettingsActivity"

function Invoke-Adb {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$AdbArgs)
    & adb.exe -s $Serial @AdbArgs
}

function Test-HuListening {
    # Require LISTEN — a plain ":5277" match false-positives on TIME_WAIT / forwards.
    $out = (Invoke-Adb shell "ss -lntp 2>/dev/null; netstat -an 2>/dev/null" 2>$null) -join "`n"
    return [bool]($out -match "LISTEN[^\r\n]*:$Port|:$Port\s+[^\r\n]*LISTEN")
}

function Wait-HuListening([int]$Seconds) {
    $deadline = (Get-Date).AddSeconds($Seconds)
    while ((Get-Date) -lt $deadline) {
        if (Test-HuListening) { return $true }
        Start-Sleep -Milliseconds 400
    }
    return Test-HuListening
}

function Start-HuService {
    Write-Host "Starting DeveloperHeadUnitNetworkService ..." -ForegroundColor Cyan
    $r = (Invoke-Adb shell am start-foreground-service -n $svc 2>&1) -join "`n"
    if ($r -match "Error|Exception|not found|Unable|Permission") {
        $r = (Invoke-Adb shell am startservice -n $svc 2>&1) -join "`n"
    }
    Write-Host $r -ForegroundColor DarkGray
}

function Tap-UiText([string]$Text) {
    $remote = "/sdcard/aa-hu-ui.xml"
    $local = Join-Path $env:TEMP "aa-hu-ui.xml"
    Invoke-Adb shell uiautomator dump $remote | Out-Null
    Invoke-Adb pull $remote $local | Out-Null
    if (-not (Test-Path $local)) { return $false }
    $ui = Get-Content $local -Raw -ErrorAction SilentlyContinue
    if (-not $ui) { return $false }
    $escaped = [regex]::Escape($Text)
    $m = [regex]::Match($ui, "(?i)(?:text|content-desc)=`"[^`"]*$escaped[^`"]*`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]")
    if (-not $m.Success) {
        $m = [regex]::Match($ui, "(?i)bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\][^>]*(?:text|content-desc)=`"[^`"]*$escaped[^`"]*`"")
    }
    if (-not $m.Success) { return $false }
    $x = [int](([int]$m.Groups[1].Value + [int]$m.Groups[3].Value) / 2)
    $y = [int](([int]$m.Groups[2].Value + [int]$m.Groups[4].Value) / 2)
    Invoke-Adb shell input tap $x $y | Out-Null
    return $true
}

function Start-HuViaUi {
    Write-Host "UI Automator: Developer settings → Start head unit server ..." -ForegroundColor Cyan
    Invoke-Adb shell input keyevent KEYCODE_WAKEUP | Out-Null
    Invoke-Adb shell wm dismiss-keyguard | Out-Null
    Invoke-Adb shell am start -n $devSettings | Out-Null
    Start-Sleep -Seconds 2
    # Already running → menu may say "Stop head unit server"
    if (Tap-UiText "Stop head unit server") {
        Write-Host "Head unit server already running (saw Stop)" -ForegroundColor DarkGray
        return $true
    }
    if (Tap-UiText "Start head unit server") {
        Start-Sleep -Seconds 1
        return $true
    }
    # Overflow / more options on some AA builds
    if ((Tap-UiText "More options") -or (Tap-UiText "More Options")) {
        Start-Sleep -Milliseconds 800
        if (Tap-UiText "Start head unit server") { return $true }
    }
    return $false
}

if (Test-HuListening) {
    Write-Host "OK  Head unit server already listening on $Port" -ForegroundColor Green
    exit 0
}

Start-HuService
if (Wait-HuListening 5) {
    Write-Host "OK  Head unit server listening on $Port (service)" -ForegroundColor Green
    exit 0
}

if (Start-HuViaUi) {
    Start-HuService
    if (Wait-HuListening $WaitSec) {
        Write-Host "OK  Head unit server listening on $Port (UI)" -ForegroundColor Green
        exit 0
    }
}

Write-Host @"
aa-start-head-unit-server: port $Port not listening after ${WaitSec}s.
On the phone: Android Auto → overflow → Start head unit server, then re-run.
"@ -ForegroundColor Red
exit 1
