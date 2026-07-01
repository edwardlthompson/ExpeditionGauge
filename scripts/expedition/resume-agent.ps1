# Print active sprint and next AGENT task; update session state.
param(
    [switch]$NoDispatchParallel
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$planPath = Join-Path $Root "BUILD_PLAN.md"
$lines = Get-Content $planPath

$activeSprint = "unknown"
$nextTask = $null
foreach ($line in $lines) {
    if ($line -match '^### Sprint (\d+[a-z]?) —') {
        $candidate = $Matches[1]
    }
    if ($line -match '^\d+\.\s+🔲 \[(AGENT|ADB|AUTO)\]' -and $null -eq $nextTask) {
        $activeSprint = $candidate
        $nextTask = $line.Trim()
    }
}
if ($activeSprint -eq "unknown" -and $candidate) {
    $activeSprint = $candidate
}

$parallelNote = $null
$manifestPath = Join-Path $Root "scripts/expedition/parallel-manifests/sprint-$activeSprint.json"
if (Test-Path $manifestPath) {
    $manifest = Get-ParallelManifest -Sprint $activeSprint
    if ((Test-ParallelLockComplete -Sprint $activeSprint -Manifest $manifest)) {
        $lockFile = Join-Path $Root ".cursor/parallel-scope-lock.json"
        if (-not (Test-Path $lockFile)) {
            $parallelNote = "Parallel lane ready for sprint $activeSprint — writing scope lock"
            if (-not $NoDispatchParallel) {
                Write-Host "$parallelNote..." -ForegroundColor Magenta
                $null = Invoke-BootstrapBash "scripts/plan-parallel-dispatch.sh" @("--write-lock", "--json", "--feature", $activeSprint)
                Write-Host "Run /scope to auto-dispatch Task subagents" -ForegroundColor Cyan
            }
        } else {
            $parallelNote = "Parallel scope lock present — run /scope to dispatch Task subagents"
        }
    }
}

$state = @{
    timestamp = (Get-Date -Format "o")
    activeSprint = $activeSprint
    nextAgentTask = $nextTask
    build_plan_lane = if ($parallelNote) { "parallel" } else { "sequential" }
}
Write-Utf8NoBom (Join-Path $Root ".cursor-session-state.json") ($state | ConvertTo-Json -Depth 3)

Write-Host "Active sprint: $activeSprint" -ForegroundColor Cyan
if ($nextTask) {
    Write-Host "Next AGENT task:" -ForegroundColor Yellow
    Write-Host $nextTask
} else {
    Write-Host "No pending AGENT tasks found."
}
if ($parallelNote) {
    Write-Host $parallelNote -ForegroundColor Magenta
}
