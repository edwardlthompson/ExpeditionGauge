#Requires -Version 5.1
<#
.SYNOPSIS
  Regenerates the slim CC0 OBDex DTC catalog asset (code -> English title).

.DESCRIPTION
  Wraps scripts/expedition/fetch-obdex-dtc.py. Source data is OBDex (CC0-1.0),
  the same catalog OBDForge uses — not GPL OBDForge code.

.EXAMPLE
  pwsh scripts/expedition/fetch-obdex-dtc.ps1
#>
$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
if (-not (Test-Path (Join-Path $Root "scripts\expedition\fetch-obdex-dtc.py"))) {
    $Root = (Resolve-Path (Join-Path $PSScriptRoot "..\..")).Path
}
Set-Location $Root
python scripts/expedition/fetch-obdex-dtc.py
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
