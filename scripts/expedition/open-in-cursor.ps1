# Open ExpeditionGauge workspace in Cursor (helper).
param()

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

if (Get-Command cursor -ErrorAction SilentlyContinue) {
    & cursor $Root
} elseif (Get-Command code -ErrorAction SilentlyContinue) {
    & code $Root
} else {
    Write-Host "Open manually: $Root"
}
