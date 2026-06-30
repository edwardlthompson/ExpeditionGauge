# Poll adb devices until hardware connected.
param(
    [int]$TimeoutSec = 120,
    [string]$Serial = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"

if (-not $Serial) {
    try { $Serial = Get-AdbSerial (Read-ProjectConfig) } catch { }
}

$deadline = (Get-Date).AddSeconds($TimeoutSec)
while ((Get-Date) -lt $deadline) {
    $devices = & adb devices 2>$null | Select-Object -Skip 1 | Where-Object { $_ -match '\tdevice' }
    if ($Serial) {
        $match = $devices | Where-Object { $_ -match "^$Serial\s" }
        if ($match) {
            Write-JsonResult @{ status = "ok"; serial = $Serial }
        }
    } elseif ($devices) {
        $s = ($devices[0] -split '\t')[0]
        Write-JsonResult @{ status = "ok"; serial = $s }
    }
    Start-Sleep -Seconds 2
}

Write-JsonResult @{ status = "blocker"; reason = "no adb device" } 2
