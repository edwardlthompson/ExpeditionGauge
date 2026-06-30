# Sprint → ADR gate map.
param(
    [Parameter(Mandatory = $true)]
    [string]$Sprint
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

$map = @{
    "1" = @("0001", "0003")
    "5b" = @("0007")
    "5c" = @("0008")
    "10" = @("0002")
    "15" = @("0004")
    "18" = @("0005")
    "19" = @("0006")
}

$key = $Sprint.ToString()
if (-not $map.ContainsKey($key)) {
    Write-Host "check-adr-gate: no ADR requirement for Sprint $Sprint"
    exit 0
}

foreach ($adr in $map[$key]) {
    $path = Join-Path $Root "docs\adr\$adr-*.md"
    $files = Get-Item $path -ErrorAction SilentlyContinue
    if (-not $files) {
        Write-Error "Missing ADR-$adr for Sprint $Sprint"
        exit 1
    }
    $content = Get-Content $files[0].FullName -Raw
    if ($content -notmatch '\*\*Status:\*\* Accepted') {
        Write-Error "ADR-$adr not Accepted — run accept-adr.ps1 -Adr $adr"
        exit 1
    }
}

Write-Host "check-adr-gate Sprint ${Sprint}: OK" -ForegroundColor Green
