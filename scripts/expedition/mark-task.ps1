# Flip task emoji markers in BUILD_PLAN.md.
param(
    [Parameter(Mandatory = $true)]
    [string]$Pattern,
    [ValidateSet("done", "pending", "progress")]
    [string]$Status = "done"
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$planPath = Join-Path $Root "BUILD_PLAN.md"
$content = Get-Content $planPath -Raw

$from = switch ($Status) {
    "done" { "🔲"; "✅" }
    "pending" { "✅"; "🔲" }
    "progress" { "🔲"; "🔄" }
}

if ($content -notmatch [regex]::Escape($Pattern)) {
    Write-Error "Pattern not found in BUILD_PLAN.md: $Pattern"
    exit 1
}

$escaped = [regex]::Escape($Pattern)
$content = [regex]::Replace($content, "🔲 \[$escaped\]", "✅ [$Pattern]", 1)
if ($content -notmatch "✅ \[$([regex]::Escape($Pattern))\]") {
    $content = [regex]::Replace($content, $escaped, $Pattern, 1)
    $content = $content -replace "🔲 \[AGENT\].*$([regex]::Escape($Pattern.Substring(0, [Math]::Min(40, $Pattern.Length))))", "✅ [AGENT] $Pattern"
}

Write-Utf8NoBom $planPath $content
Write-Host "mark-task: updated pattern matching '$Pattern'"

if ($Status -eq "done") {
    $manifestDir = Join-Path $Root "scripts/expedition/parallel-manifests"
    if (Test-Path $manifestDir) {
        Get-ChildItem $manifestDir -Filter "sprint-*.json" | ForEach-Object {
            $manifest = Get-Content $_.FullName -Raw | ConvertFrom-Json
            if ($Pattern -notmatch [regex]::Escape($manifest.sequentialLockPattern)) { return }
            $statePath = Join-Path $Root ".cursor/parallel-dispatch/sprint-$($manifest.sprint).json"
            if (Test-Path $statePath) {
                Write-Host "mark-task: parallel dispatch already recorded for sprint $($manifest.sprint)" -ForegroundColor DarkGray
                return
            }
            Write-Host "mark-task: sequential lock done — writing parallel scope lock for sprint $($manifest.sprint)..." -ForegroundColor Magenta
            $null = Invoke-BootstrapBash "scripts/plan-parallel-dispatch.sh" @("--write-lock", "--json", "--feature", $manifest.sprint)
            if ($LASTEXITCODE -ne 0) {
                Write-Warning "plan-parallel-dispatch lock write failed (exit $LASTEXITCODE) — run /scope manually"
            } else {
                Write-Host "mark-task: run /scope to auto-dispatch Task subagents (or pwsh scripts/expedition/dispatch-parallel-agents.ps1 -Sprint $($manifest.sprint))" -ForegroundColor Cyan
            }
        }
    }
}
