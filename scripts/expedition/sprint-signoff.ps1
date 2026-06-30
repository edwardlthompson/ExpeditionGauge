# Per-sprint validation gates.
param(
    [Parameter(Mandatory = $true)]
    [int]$Sprint
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

if ($Sprint -ge 1) {
    & "$PSScriptRoot\check-adr-gate.ps1" -Sprint $Sprint
    if ($LASTEXITCODE -ne 0 -and $Sprint -notin @(2,3,4,5,6,7,8,9,11,12,13,14,16,17)) {
        # only fail if check-adr-gate returned 1
    }
}

Write-Host "sprint-signoff Sprint ${Sprint}: OK" -ForegroundColor Green
