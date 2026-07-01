# Merge parallel agent branches back into the current branch (sequential owner).
param(
    [Parameter(Mandatory = $true)]
    [string]$Sprint,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
Set-Location $Root

$manifest = Get-ParallelManifest -Sprint $Sprint
$agents = @($manifest.agents | Where-Object { $_.owner -eq "AGENT" })

Write-Host "Merging $($agents.Count) parallel branches into $(git branch --show-current)..." -ForegroundColor Cyan

foreach ($agent in $agents) {
    $branch = $agent.branch
    $exists = git rev-parse --verify $branch 2>$null
    if (-not $exists) {
        Write-Warning "Branch not found, skipping: $branch"
        continue
    }
    if ($DryRun) {
        Write-Host "[dry-run] git merge --no-ff $branch" -ForegroundColor Yellow
        continue
    }
    Write-Host "Merging $branch ..." -ForegroundColor Cyan
    & git merge --no-ff $branch -m "merge(parallel): sprint $Sprint agent $($agent.id) ($($agent.slug))"
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Merge failed for $branch — resolve conflicts then re-run"
        exit 1
    }
}

if (-not $DryRun) {
    foreach ($agent in $agents) {
        $wt = Join-Path $Root ".cursor/worktrees/sprint-$Sprint/$($agent.slug)"
        if (Test-Path $wt) {
            & git worktree remove $wt --force 2>$null
        }
    }
    Write-Host "Parallel merge complete for sprint $Sprint" -ForegroundColor Green
    Write-Host "Next: resume sequential lane step $($manifest.mergeBeforeStep) (tests / merge gate)"
}
