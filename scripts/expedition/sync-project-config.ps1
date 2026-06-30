# Sync project.config.json into bootstrap placeholders and assets.
param()

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
Set-Location $Root

$config = Read-ProjectConfig

$initPrompt = Join-Path $Root "docs\INITIALIZATION_PROMPT.md"
if (Test-Path $initPrompt) {
    $text = Get-Content $initPrompt -Raw
    $text = $text -replace '\[INSERT PROJECT NAME HERE\]', $config.projectName
    $text = $text -replace '\[INSERT ONE-LINE PURPOSE HERE\]', $config.purpose
    $text = $text -replace '\[INSERT PLATFORM / TECH STACK HERE\]', $config.stack
    if ($config.releaseRepo) {
        $text = $text -replace '\[INSERT RELEASE REPO HERE\]', $config.releaseRepo
    }
    Write-Utf8NoBom $initPrompt $text
}

if ($config.donationsUrl) {
    $donPath = Join-Path $Root "examples\android\app\src\main\assets\donations.json"
    if (Test-Path $donPath) {
        $don = Get-Content $donPath -Raw | ConvertFrom-Json
        $don.url = $config.donationsUrl
        Write-Utf8NoBom $donPath ($don | ConvertTo-Json -Depth 5)
    }
}

python3 scripts/sync-stack-config.py $Root $config.releaseRepo $config.donationsUrl 2>$null
python3 scripts/sync-design-tokens.py 2>$null

Write-Host "sync-project-config: OK" -ForegroundColor Green
