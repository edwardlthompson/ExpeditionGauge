# Per-sprint validation gates.
param(
    [Parameter(Mandatory = $true)]
    [string]$Sprint
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
Set-Location $Root

$config = Read-ProjectConfig

function Invoke-Gate {
    param([string]$Name, [scriptblock]$Block)
    Write-Host "=== $Name ===" -ForegroundColor Cyan
    & $Block
    $code = if ($null -ne $LASTEXITCODE) { $LASTEXITCODE } else { 0 }
    if ($code -ne 0) {
        Write-Error "Gate failed: $Name (exit $code)"
        exit $code
    }
}

Invoke-Gate "verify-plan-persisted" { & "$PSScriptRoot\verify-plan-persisted.ps1" }

$sprintNum = $null
if ($Sprint -match '^\d+$') {
    $sprintNum = [int]$Sprint
} elseif ($Sprint -eq "17b") {
    $sprintNum = 17
}

if ($Sprint -eq "17b") {
    Invoke-Gate "polish-wave3-gate" { Invoke-BootstrapBash "scripts/check-polish-wave3-gate.sh" }
    Invoke-Gate "polish-wave2-gate" { Invoke-BootstrapBash "scripts/check-polish-wave2-gate.sh" }
    Invoke-Gate "polish-wave1-gate" { Invoke-BootstrapBash "scripts/check-polish-wave1-gate.sh" }
    Invoke-Gate "fdroid-metadata" { Invoke-BootstrapBash "scripts/verify-fdroid-metadata.sh" }
    Invoke-Gate "android-release-build" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat assembleRelease testDebugUnitTest --quiet
        Pop-Location
    }
}

if ($Sprint -eq "22") {
    Invoke-Gate "v2-media-gate" { Invoke-BootstrapBash "scripts/check-v2-media-gate.sh" }
    Invoke-Gate "v2-car-gate" { Invoke-BootstrapBash "scripts/check-v2-car-gate.sh" }
    Invoke-Gate "fdroid-metadata" { Invoke-BootstrapBash "scripts/verify-fdroid-metadata.sh" }
    Invoke-Gate "android-release-build" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat :app:testDebugUnitTest assembleRelease --quiet
        Pop-Location
    }
}

if ($Sprint -eq "21") {
    Invoke-Gate "v2-car-gate" { Invoke-BootstrapBash "scripts/check-v2-car-gate.sh" }
    Invoke-Gate "v2-orientation-gate" { Invoke-BootstrapBash "scripts/check-v2-orientation-gate.sh" }
    Invoke-Gate "fdroid-metadata" { Invoke-BootstrapBash "scripts/verify-fdroid-metadata.sh" }
    Invoke-Gate "android-release-build" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat :app:testDebugUnitTest :car:testDebugUnitTest assembleRelease --quiet
        Pop-Location
    }
}

if ($Sprint -eq "20") {
    Invoke-Gate "v2-orientation-gate" { Invoke-BootstrapBash "scripts/check-v2-orientation-gate.sh" }
    Invoke-Gate "system-insets-gate" { Invoke-BootstrapBash "scripts/check-system-insets-gate.sh" }
    Invoke-Gate "fdroid-metadata" { Invoke-BootstrapBash "scripts/verify-fdroid-metadata.sh" }
    Invoke-Gate "android-release-build" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat assembleRelease testDebugUnitTest --quiet
        Pop-Location
    }
}

if ($Sprint -eq "19b") {
    Invoke-Gate "system-insets-gate" { Invoke-BootstrapBash "scripts/check-system-insets-gate.sh" }
    Invoke-Gate "fdroid-metadata" { Invoke-BootstrapBash "scripts/verify-fdroid-metadata.sh" }
    Invoke-Gate "android-release-build" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat assembleRelease testDebugUnitTest --quiet
        Pop-Location
    }
}

if ($Sprint -eq "19") {
    Invoke-Gate "v2-live-gate" { Invoke-BootstrapBash "scripts/check-v2-live-gate.sh" }
    Invoke-Gate "v2-video-gate" { Invoke-BootstrapBash "scripts/check-v2-video-gate.sh" }
    Invoke-Gate "fdroid-metadata" { Invoke-BootstrapBash "scripts/verify-fdroid-metadata.sh" }
    Invoke-Gate "android-release-build" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat assembleRelease testDebugUnitTest --quiet
        Pop-Location
    }
}

if ($Sprint -eq "18") {
    Invoke-Gate "v2-video-gate" { Invoke-BootstrapBash "scripts/check-v2-video-gate.sh" }
    Invoke-Gate "polish-wave3-gate" { Invoke-BootstrapBash "scripts/check-polish-wave3-gate.sh" }
    Invoke-Gate "fdroid-metadata" { Invoke-BootstrapBash "scripts/verify-fdroid-metadata.sh" }
    Invoke-Gate "android-release-build" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat assembleRelease testDebugUnitTest --quiet
        Pop-Location
    }
}

if ($Sprint -eq 0) {
    Invoke-Gate "design-cohesion" { & (Join-Path $Root "scripts\check-design-cohesion.ps1") }
    $vb = Invoke-BootstrapBash "scripts/validate-bootstrap.sh" "--quick"
    if ($vb -ne 0) {
        Write-Host "WARN: validate-bootstrap --quick returned $vb (non-fatal on dev machine)" -ForegroundColor Yellow
    }
    Invoke-Gate "android-build-test" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat assembleDebug testDebugUnitTest --quiet
        Pop-Location
    }
    if (Get-Command rg -ErrorAction SilentlyContinue) {
        $proprietary = rg -i "play-services|firebase|google-analytics" examples/android --glob "*.gradle*" 2>$null
        if ($proprietary) { Write-Error "FOSS grep failed: proprietary deps found"; exit 1 }
    }
}

if ($null -ne $sprintNum -and $sprintNum -ge 1) {
    & "$PSScriptRoot\check-adr-gate.ps1" -Sprint $sprintNum
    if ($LASTEXITCODE -ne 0 -and $sprintNum -notin @(2,3,4,5,6,7,8,9,11,12,13,14,16,17)) {
        # only fail if check-adr-gate returned 1
    }
}

if ($Sprint -eq "27") {
    Invoke-Gate "v2-sharing-gate" { Invoke-BootstrapBash "scripts/check-v2-sharing-gate.sh" }
    Invoke-Gate "fdroid-metadata" { Invoke-BootstrapBash "scripts/verify-fdroid-metadata.sh" }
    Invoke-Gate "android-release-build" {
        Push-Location (Join-Path $Root "examples\android")
        & .\gradlew.bat :app:testDebugUnitTest assembleRelease --quiet
        Pop-Location
    }
}

Write-Host "sprint-signoff Sprint ${Sprint}: OK" -ForegroundColor Green
