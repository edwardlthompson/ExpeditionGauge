# Generate Android Auto glance-tile PNG previews for Cursor review (no DHU required).
#
# Writes:
#   .cursor/screenshots/aa-tile-attitude.png
#   .cursor/screenshots/aa-tile-telemetry.png
#   .cursor/screenshots/aa-tile-tpms.png
#
# Usage:
#   pwsh scripts/expedition/aa-bitmap-preview.ps1
#   pwsh scripts/expedition/aa-bitmap-preview.ps1 -OutDir path\to\dir
param(
    [string]$OutDir = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

if (-not $OutDir) {
    $OutDir = Join-Path $Root ".cursor/screenshots"
}
New-Item -ItemType Directory -Force -Path $OutDir | Out-Null
$OutDir = (Resolve-Path $OutDir).Path

$android = Join-Path $Root "examples/android"
$gradlew = Join-Path $android "gradlew.bat"
if (-not (Test-Path $gradlew)) {
    Write-Error "aa-bitmap-preview: missing examples/android/gradlew.bat"
}

Write-Host "Writing AA tile PNGs to $OutDir ..." -ForegroundColor Cyan
Push-Location $android
try {
    & .\gradlew.bat :car:testDebugUnitTest `
        --tests "dev.foss.expeditiongauge.car.gauge.AaTileBitmapPreviewWriterTest" `
        "-PaaPreviewDir=$OutDir"
    if ($LASTEXITCODE -ne 0) {
        throw "aa-bitmap-preview: gradle test failed ($LASTEXITCODE)"
    }
} finally {
    Pop-Location
}

foreach ($name in @("aa-tile-attitude.png", "aa-tile-telemetry.png", "aa-tile-tpms.png", "aa-drive-hud.png", "aa-drive-hud-light.png")) {
    $path = Join-Path $OutDir $name
    if (-not (Test-Path $path)) {
        Write-Error "aa-bitmap-preview: expected output missing: $path"
    }
    Write-Host "OK  $path" -ForegroundColor Green
}

Write-Host "Open the PNGs in Cursor to review projected-AA glance tiles." -ForegroundColor Cyan
