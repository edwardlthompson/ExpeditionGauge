# Print active sprint and next AGENT task; update session state.
param()

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$planPath = Join-Path $Root "BUILD_PLAN.md"
$lines = Get-Content $planPath

$activeSprint = "unknown"
$nextTask = $null
foreach ($line in $lines) {
    if ($line -match '^### Sprint (\d+[a-z]?) —') {
        $activeSprint = $Matches[1]
    }
    if ($line -match '🔲 \[AGENT\]') {
        $nextTask = $line.Trim()
        break
    }
}

$state = @{
    timestamp = (Get-Date -Format "o")
    activeSprint = $activeSprint
    nextAgentTask = $nextTask
}
Write-Utf8NoBom (Join-Path $Root ".cursor-session-state.json") ($state | ConvertTo-Json -Depth 3)

Write-Host "Active sprint: $activeSprint" -ForegroundColor Cyan
if ($nextTask) {
    Write-Host "Next AGENT task:" -ForegroundColor Yellow
    Write-Host $nextTask
} else {
    Write-Host "No pending AGENT tasks found."
}
