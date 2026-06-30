# Capture F-Droid phone screenshots from a connected device.
param(
    [string]$Serial = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial $config }

$pkg = "dev.foss.expeditiongauge"
$outDir = Join-Path $Root "examples\android\metadata\en-US\images\phoneScreenshots"
$fastlaneDir = Join-Path $Root "examples\android\fastlane\metadata\android\en-US\images\phoneScreenshots"
New-Item -ItemType Directory -Force -Path $outDir, $fastlaneDir | Out-Null

function Invoke-AdbShell {
    param([string]$Command)
    if ($Serial) { & adb -s $Serial shell $Command } else { & adb shell $Command }
}

function Invoke-AdbPull {
    param([string]$Remote, [string]$Local)
    if ($Serial) { & adb -s $Serial pull $Remote $Local } else { & adb pull $Remote $Local }
}

function Capture-Screen {
    param([string]$Name)
    $remote = "/sdcard/eg_fdroid_cap.png"
    $local = Join-Path $outDir $Name
    Invoke-AdbShell "screencap -p $remote" | Out-Null
    Invoke-AdbPull $remote $local | Out-Null
    Copy-Item -Force $local (Join-Path $fastlaneDir $Name)
    Write-Host "Captured $Name"
}

function Grant-Perms {
    foreach ($perm in @(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.BLUETOOTH_SCAN"
    )) {
        Invoke-AdbShell "pm grant $pkg $perm" 2>$null | Out-Null
    }
}

function Dismiss-Onboarding {
    Invoke-AdbShell "uiautomator dump /sdcard/window_dump.xml" | Out-Null
    $dump = Join-Path $env:TEMP "eg-cap-dump.xml"
    Invoke-AdbPull "/sdcard/window_dump.xml" $dump | Out-Null
    $content = Get-Content $dump -Raw
    if ($content -match 'text="Skip tour"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
        $x = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
        $y = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
        Invoke-AdbShell "input tap $x $y" | Out-Null
        Start-Sleep -Seconds 2
    }
}

function Ensure-Session {
    Invoke-AdbShell "am force-stop $pkg" | Out-Null
    Grant-Perms
    Invoke-AdbShell "am start -n $pkg/.MainActivity" | Out-Null
    Start-Sleep -Seconds 4
    Dismiss-Onboarding
    Invoke-AdbShell "uiautomator dump /sdcard/window_dump.xml" | Out-Null
    $dump = Join-Path $env:TEMP "eg-cap-dump.xml"
    Invoke-AdbPull "/sdcard/window_dump.xml" $dump | Out-Null
    $content = Get-Content $dump -Raw
    if ($content -match 'text="Record"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
        $x = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
        $y = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
        Invoke-AdbShell "input tap $x $y" | Out-Null
        Start-Sleep -Seconds 10
        Invoke-AdbShell "uiautomator dump /sdcard/window_dump.xml" | Out-Null
        Invoke-AdbPull "/sdcard/window_dump.xml" $dump | Out-Null
        $content = Get-Content $dump -Raw
        if ($content -match 'text="Stop"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
            $sx = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
            $sy = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
            Invoke-AdbShell "input tap $sx $sy" | Out-Null
            Start-Sleep -Seconds 2
        }
    }
}

if (-not (Get-Command adb -ErrorAction SilentlyContinue)) {
    Write-Error "adb not found"
    exit 2
}

Ensure-Session
Invoke-AdbShell "am force-stop $pkg" | Out-Null
Grant-Perms
Invoke-AdbShell "am start -n $pkg/.MainActivity" | Out-Null
Start-Sleep -Seconds 4
Dismiss-Onboarding
Capture-Screen "01_dashboard_hud.png"

# Open playback
Invoke-AdbShell "uiautomator dump /sdcard/window_dump.xml" | Out-Null
$dump = Join-Path $env:TEMP "eg-cap-dump.xml"
Invoke-AdbPull "/sdcard/window_dump.xml" $dump | Out-Null
$content = Get-Content $dump -Raw
if ($content -match 'text="Sessions"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
    $x = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
    $y = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
    Invoke-AdbShell "input tap $x $y" | Out-Null
    Start-Sleep -Seconds 2
    Invoke-AdbShell "uiautomator dump /sdcard/window_dump.xml" | Out-Null
    Invoke-AdbPull "/sdcard/window_dump.xml" $dump | Out-Null
    $content = Get-Content $dump -Raw
    if ($content -match 'text="(20\d{2}-\d{2}-\d{2}[^"]*)"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
        $x = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
        $y = [int](([int]$Matches[3] + [int]$Matches[5]) / 2)
        Invoke-AdbShell "input tap $x $y" | Out-Null
        Start-Sleep -Seconds 5
    }
}

# Scroll to telemetry graphs
Invoke-AdbShell "input swipe 800 1200 800 300 350" | Out-Null
Start-Sleep -Seconds 2
Capture-Screen "02_playback_graphs.png"

# Heatmap chips near bottom
Invoke-AdbShell "input swipe 800 300 800 1200 350" | Out-Null
Start-Sleep -Seconds 1
Capture-Screen "03_playback_heatmap.png"

# Enable ghost lap
Invoke-AdbShell "uiautomator dump /sdcard/window_dump.xml" | Out-Null
Invoke-AdbPull "/sdcard/window_dump.xml" $dump | Out-Null
$content = Get-Content $dump -Raw
if ($content -match 'text="Ghost lap"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
    $x = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
    $y = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
    Invoke-AdbShell "input tap $x $y" | Out-Null
    Start-Sleep -Seconds 1
    Invoke-AdbShell "input swipe 800 1200 800 400 350" | Out-Null
    Start-Sleep -Seconds 1
}
Capture-Screen "04_playback_ghost_lap.png"

Write-Host "F-Droid screenshots captured to $outDir" -ForegroundColor Green
