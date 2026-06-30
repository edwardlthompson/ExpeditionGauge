# ADB smoke scenarios per sprint.
param(
    [Parameter(Mandatory = $true)]
    [int]$Sprint,
    [Parameter(Mandatory = $true)]
    [ValidateSet("cold-start", "calibrate-level", "drift-simulation", "tpms-pair", "external-gps")]
    [string]$Scenario,
    [string]$Serial = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

if (-not (Get-Command adb -ErrorAction SilentlyContinue)) {
    Write-JsonResult @{ status = "blocker"; reason = "adb not installed"; scenario = $Scenario } 2
}

$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial $config }

$devices = & adb devices 2>$null | Select-Object -Skip 1 | Where-Object { $_ -match '\tdevice' }
if (-not $devices -or ($Serial -and -not ($devices | Where-Object { $_ -match "^$Serial\s" }))) {
    Write-JsonResult @{ status = "blocker"; reason = "no device"; scenario = $Scenario; sprint = $Sprint } 2
}

$pkg = "dev.foss.expeditiongauge"

function Invoke-AdbCommand {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$AdbArgs)
    if ($Serial) {
        & adb -s $Serial @AdbArgs
    } else {
        & adb @AdbArgs
    }
    return $LASTEXITCODE
}

switch ($Scenario) {
    "cold-start" {
        Invoke-AdbCommand shell am force-stop $pkg | Out-Null
        Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
        Start-Sleep -Seconds 3
        $dump = Invoke-AdbCommand shell dumpsys activity activities 2>$null | Out-String
        if ($dump -notmatch [regex]::Escape($pkg)) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "activity not visible" } 1
        }
    }
    default {
        Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
        Start-Sleep -Seconds 2
    }
}

Write-JsonResult @{
    status = "ok"
    scenario = $Scenario
    sprint = $Sprint
    serial = $Serial
    package = $pkg
}
