# Poll parallel agent processes until all finish.
param(
    [Parameter(Mandatory = $true)]
    [string]$Sprint,
    [int]$PollSeconds = 15
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$statePath = Join-Path $Root ".cursor/parallel-dispatch/sprint-$Sprint.json"

if (-not (Test-Path $statePath)) {
    Write-Error "No dispatch state at $statePath — run dispatch-parallel-agents.ps1 first"
    exit 1
}

$state = Get-Content $statePath -Raw | ConvertFrom-Json
if ($state.runtime -eq "sdk") {
    $failed = @($state.agents | Where-Object { $_.status -ne "finished" })
    if ($failed.Count -gt 0) {
        Write-Error "SDK agents failed: $($failed.slug -join ', ')"
        exit 2
    }
    Write-Host "All SDK agents finished for sprint $Sprint" -ForegroundColor Green
    exit 0
}

$running = $true
while ($running) {
    $running = $false
    foreach ($agent in $state.agents) {
        if ($agent.status -eq "running") {
            $proc = Get-Process -Id $agent.pid -ErrorAction SilentlyContinue
            if ($proc) {
                $running = $true
                Write-Host "  Agent $($agent.id) ($($agent.slug)) still running (pid $($agent.pid))..."
            } else {
                $agent.status = "finished"
                Write-Host "  Agent $($agent.id) ($($agent.slug)) finished" -ForegroundColor Green
            }
        }
    }
    if ($running) { Start-Sleep -Seconds $PollSeconds }
}

$state.finished_at = (Get-Date -Format "o")
Write-Utf8NoBom $statePath ($state | ConvertTo-Json -Depth 6)

$manifest = Get-ParallelManifest -Sprint $Sprint
foreach ($auto in @($manifest.postParallelAuto)) {
    if ($auto.owner -ne "AUTO") { continue }
    $scriptPath = Join-Path $Root ($auto.script -replace '/', '\')
    if (Test-Path $scriptPath) {
        Write-Host "Running post-parallel AUTO: $($auto.script)" -ForegroundColor Cyan
        & pwsh $scriptPath
    }
}

Write-Host "All parallel agents finished for sprint $Sprint" -ForegroundColor Green
