# Accept ADR(s) by setting Status to Accepted with today's date.
param(
    [Parameter(Mandatory = $true)]
    [string[]]$Adr
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$today = Get-Date -Format "yyyy-MM-dd"

$adrIds = @()
foreach ($item in $Adr) {
    $adrIds += ($item -split ',') | ForEach-Object { $_.Trim() } | Where-Object { $_ }
}

foreach ($id in $adrIds) {
    $num = $id.PadLeft(4, '0')
    $path = Join-Path $Root "docs\adr\$num-*.md"
    $files = Get-Item $path -ErrorAction SilentlyContinue
    if (-not $files) {
        Write-Error "ADR $num not found"
        exit 1
    }
    $file = $files[0].FullName
    $content = Get-Content $file -Raw
    $content = $content -replace '\*\*Status:\*\* Proposed', '**Status:** Accepted'
    $content = $content -replace '- \*\*Status:\*\* Proposed.*', '- **Status:** Accepted'
    $content = $content -replace '- \*\*Date:\*\* YYYY-MM-DD', "- **Date:** $today"
    $content = $content -replace '\*\*Selected pattern:\*\* 🔲 MVVM', '**Selected pattern:** MVVM ✓'
    Write-Utf8NoBom $file $content
    Write-Host "Accepted ADR-$num"
}
