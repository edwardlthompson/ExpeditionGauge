# Verify BUILD_PLAN.md and project.config.json are present and valid.
param()

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

$required = @(
    "BUILD_PLAN.md",
    "project.config.json",
    "docs/START_HERE.md",
    "docs/DEV_DEVICE.md",
    "docs/RECOMMENDATIONS.md",
    ".cursor/rules/expeditiongauge-plan.mdc"
)

$missing = @()
foreach ($f in $required) {
    if (-not (Test-Path (Join-Path $Root $f))) { $missing += $f }
}
if ($missing.Count -gt 0) {
    Write-Error "Missing canonical files: $($missing -join ', ')"
    exit 1
}

$config = Read-ProjectConfig
if (-not $config.projectName -or -not $config.stack) {
    Write-Error "Invalid project.config.json"
    exit 1
}

$plan = Get-Content (Join-Path $Root "BUILD_PLAN.md") -Raw
$sections = @(
    "Automation-first: zero",
    "Sprint 0",
    "Sprint 1",
    "Architecture decision"
)
foreach ($s in $sections) {
    if ($plan -notmatch [regex]::Escape($s)) {
        Write-Error "BUILD_PLAN.md missing section: $s"
        exit 1
    }
}

$humanTaskRows = Select-String -Path (Join-Path $Root "BUILD_PLAN.md") -Pattern '^\d+\.\s+🔲\s+\[HUMAN\]' -AllMatches
if ($humanTaskRows) {
    Write-Error "BUILD_PLAN.md contains forbidden [HUMAN] task rows"
    exit 1
}

Write-Host "verify-plan-persisted: OK" -ForegroundColor Green
