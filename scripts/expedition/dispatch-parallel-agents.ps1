# Dispatch BUILD_PLAN Parallel lane agents (worktrees + headless Cursor CLI or SDK).
param(
    [Parameter(Mandatory = $true)]
    [string]$Sprint,
    [switch]$SkipLockCheck,
    [switch]$DryRun,
    [switch]$InstallCli,
    [switch]$UseSdk,
    [switch]$Wait
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
Set-Location $Root

$manifest = Get-ParallelManifest -Sprint $Sprint
$scopeExit = Invoke-BootstrapBash "scripts/check-parallel-scope.sh"
if ($scopeExit -ne 0) {
    Write-Error "Parallel scope check failed — fix BUILD_PLAN overlaps before dispatch"
    exit 1
}

if (-not $SkipLockCheck) {
    if (-not (Test-ParallelLockComplete -Sprint $Sprint -Manifest $manifest)) {
        Write-Error "Sequential lock step $($manifest.sequentialLockStep) not complete. Finish it first or pass -SkipLockCheck"
        exit 2
    }
}

$agents = @($manifest.agents | Where-Object { $_.owner -eq "AGENT" })
if ($agents.Count -eq 0) {
    Write-Error "No AGENT rows in manifest for sprint $Sprint"
    exit 1
}

$dispatchDir = Join-Path $Root ".cursor/parallel-dispatch"
$promptDir = Join-Path $Root ".cursor/parallel-prompts/sprint-$Sprint"
$worktreesRoot = Join-Path $Root ".cursor/worktrees/sprint-$Sprint"
$logsDir = Join-Path $dispatchDir "logs/sprint-$Sprint"
foreach ($dir in @($dispatchDir, $promptDir, $worktreesRoot, $logsDir)) {
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
}

function New-ParallelPrompt {
    param($Agent, $Manifest)
    $scopes = ($Agent.scopes | ForEach-Object { "- ``$_``" }) -join "`n"
    $also = @($Agent.alsoEdit)
    $alsoBlock = ""
    if ($also.Count -gt 0) {
        $alsoLines = ($also | ForEach-Object { "- ``$_``" }) -join "`n"
        $alsoBlock = @"

## Also edit (same agent)
$alsoLines
"@
    }
    $forbidden = ($Manifest.sequentialOnly | ForEach-Object { "- ``$_``" }) -join "`n"
    $spec = $Agent.spec
    $specLine = if ($spec) { "Read ``$spec`` first if it exists.`n`n" } else { "" }
    return @"
# Parallel agent task — Sprint $($Manifest.sprint) / $($Agent.id)

${specLine}## Task
$($Agent.task)

## Allowed files (ONLY edit these)
$scopes
$alsoBlock

## Forbidden (sequential owner only)
$forbidden

## Rules
- Do NOT edit BUILD_PLAN.md or files outside allowed scope.
- Package namespace: dev.foss.expeditiongauge
- After edits run: bash scripts/watch-agent-gates.sh --once --autofix
- Commit on branch ``$($Agent.branch)`` with a Conventional Commit message.

Implement now. Apply all file changes.
"@
}

function Ensure-GitWorktree {
    param(
        [string]$Branch,
        [string]$WorktreePath
    )
    if (Test-Path (Join-Path $WorktreePath ".git")) { return }
    if (Test-Path $WorktreePath) {
        throw "Path exists but is not a git worktree: $WorktreePath"
    }
    & git worktree add -B $Branch $WorktreePath HEAD
    if ($LASTEXITCODE -ne 0) { throw "git worktree add failed for $Branch" }
}

if ($DryRun) {
    foreach ($agent in $agents) {
        $slug = $agent.slug
        Write-Host "[dry-run] Agent $($agent.id): branch=$($agent.branch) slug=$slug" -ForegroundColor Yellow
        $promptPath = Join-Path $promptDir "$slug.md"
        Write-Utf8NoBom $promptPath (New-ParallelPrompt -Agent $agent -Manifest $manifest)
    }
    Write-Host "Dry run complete — $($agents.Count) agents would launch" -ForegroundColor Green
    exit 0
}

# --- SDK path (parallel Agent.prompt via Python) ---
if ($UseSdk -or ($env:CURSOR_API_KEY -and -not (Get-CursorAgentCli))) {
    if ($DryRun) {
        Write-Host "[dry-run] Would dispatch $($agents.Count) SDK agents for sprint $Sprint" -ForegroundColor Yellow
        exit 0
    }
    $py = if (Get-Command python3 -ErrorAction SilentlyContinue) { "python3" }
          elseif (Get-Command python -ErrorAction SilentlyContinue) { "python" }
          else { $null }
    if (-not $py) { Write-Error "Python required for SDK dispatch (pip install cursor-sdk)"; exit 2 }
    & $py (Join-Path $PSScriptRoot "dispatch_parallel_agents.py") --sprint $Sprint --max-workers $agents.Count
    $code = $LASTEXITCODE
    if ($code -ne 0) { exit $code }
    Write-Host "SDK parallel dispatch finished for sprint $Sprint" -ForegroundColor Green
    exit 0
}

# --- Cursor headless CLI path ---
$agentCli = Get-CursorAgentCli
if (-not $agentCli -and $InstallCli) {
    Write-Host "Installing Cursor CLI..." -ForegroundColor Cyan
    irm 'https://cursor.com/install?win32=true' | iex
    $agentCli = Get-CursorAgentCli
}

if (-not $agentCli) {
    Write-Error @"
Cursor headless CLI not found. Options:
  1. Install CLI:  pwsh scripts/expedition/dispatch-parallel-agents.ps1 -Sprint $Sprint -InstallCli
  2. SDK dispatch: `$env:CURSOR_API_KEY='...'; pip install cursor-sdk; ... -UseSdk
  3. Set path:     `$env:CURSOR_AGENT_BIN='C:\path\to\agent.exe'
See https://cursor.com/docs/cli/headless
"@
    exit 2
}

if (-not $env:CURSOR_API_KEY) {
    Write-Warning "CURSOR_API_KEY is not set — headless agent may fail auth"
}

$stateAgents = @()
$processes = @()

foreach ($agent in $agents) {
    $slug = $agent.slug
    $branch = $agent.branch
    $worktree = Join-Path $worktreesRoot $slug
    $promptPath = Join-Path $promptDir "$slug.md"
    $logPath = Join-Path $logsDir "$slug.log"
    $prompt = New-ParallelPrompt -Agent $agent -Manifest $manifest
    Write-Utf8NoBom $promptPath $prompt

    Ensure-GitWorktree -Branch $branch -WorktreePath $worktree
    $taskCopy = Join-Path $worktree ".cursor/PARALLEL_AGENT_TASK.md"
    $taskDir = Split-Path $taskCopy -Parent
    if (-not (Test-Path $taskDir)) { New-Item -ItemType Directory -Path $taskDir -Force | Out-Null }
    Write-Utf8NoBom $taskCopy $prompt

    $promptRef = ".cursor/PARALLEL_AGENT_TASK.md"
    $args = @(
        "-p", "--force", "--output-format", "text",
        "Execute the parallel agent task in @$promptRef"
    )

    Write-Host "Launching agent $($agent.id) ($slug) on branch $branch..." -ForegroundColor Cyan
    $proc = Start-Process -FilePath $agentCli -ArgumentList $args `
        -WorkingDirectory $worktree `
        -RedirectStandardOutput $logPath `
        -RedirectStandardError (Join-Path $logsDir "$slug.err.log") `
        -PassThru -NoNewWindow

    $stateAgents += @{
        id = $agent.id
        slug = $slug
        branch = $branch
        worktree = ".cursor/worktrees/sprint-$Sprint/$slug"
        pid = $proc.Id
        log = ".cursor/parallel-dispatch/logs/sprint-$Sprint/$slug.log"
        status = "running"
        started_at = (Get-Date -Format "o")
    }
    $processes += $proc
}

$state = @{
    sprint = $Sprint
    dispatched_at = (Get-Date -Format "o")
    runtime = "cli"
    agent_cli = $agentCli
    agents = $stateAgents
}
$statePath = Join-Path $dispatchDir "sprint-$Sprint.json"
Write-Utf8NoBom $statePath ($state | ConvertTo-Json -Depth 6)

# Update session state for restore
$sessionPath = Join-Path $Root ".cursor-session-state.json"
$session = @{
    timestamp = (Get-Date -Format "o")
    activeSprint = $Sprint
    build_plan_lane = "parallel"
    parallel_tasks_in_flight = @($stateAgents | ForEach-Object { $_.slug })
    sequential_step = $manifest.sequentialLockStep
    notes = "Parallel dispatch sprint $Sprint — $($agents.Count) agents running"
}
Write-Utf8NoBom $sessionPath ($session | ConvertTo-Json -Depth 4)

Write-Host "Dispatched $($agents.Count) parallel agents for sprint $Sprint" -ForegroundColor Green
Write-Host "State: $statePath"
Write-Host "Wait:  pwsh scripts/expedition/wait-parallel-agents.ps1 -Sprint $Sprint"
Write-Host "Merge: pwsh scripts/expedition/merge-parallel-agents.ps1 -Sprint $Sprint"

if ($Wait) {
    & "$PSScriptRoot/wait-parallel-agents.ps1" -Sprint $Sprint
    exit $LASTEXITCODE
}
