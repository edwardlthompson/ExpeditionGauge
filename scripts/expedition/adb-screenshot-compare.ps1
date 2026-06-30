# Pull dashboard screenshot and compare layout reference (Sprint 2).
param(
    [Parameter(Mandatory = $true)]
    [int]$Sprint,
    [string]$Serial = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial $config }

$devices = & adb devices 2>$null | Select-Object -Skip 1 | Where-Object { $_ -match '\tdevice' }
if (-not $devices) {
    Write-JsonResult @{ status = "blocker"; reason = "no device"; sprint = $Sprint } 2
}

$outDir = Join-Path $Root "docs\design\gauge-reference\screenshots"
New-Item -ItemType Directory -Path $outDir -Force | Out-Null
$shot = Join-Path $outDir "sprint$Sprint-device.png"
$ref = Join-Path $Root "docs\design\gauge-reference\hud-reference.png"

$pkg = "dev.foss.expeditiongauge"

function Invoke-AdbCommand {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$AdbArgs)
    if ($Serial) {
        & adb -s $Serial @AdbArgs
    } else {
        & adb @AdbArgs
    }
}

Invoke-AdbCommand shell screencap -p /sdcard/expedition_hud.png | Out-Null
Invoke-AdbCommand pull /sdcard/expedition_hud.png $shot | Out-Null

$result = @{
    status = "ok"
    sprint = $Sprint
    screenshot = $shot
    reference = $ref
    referenceExists = (Test-Path $ref)
}
Write-JsonResult $result 0
