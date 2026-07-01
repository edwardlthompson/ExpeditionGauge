# Shared helpers for ExpeditionGauge automation scripts.
$script:ExpeditionRoot = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)

function Get-ExpeditionRoot {
    if ($env:EXPEDITION_GAUGE_ROOT) { return $env:EXPEDITION_GAUGE_ROOT }
    return $script:ExpeditionRoot
}

function Write-Utf8NoBom {
    param([string]$Path, [string]$Content)
    $dir = Split-Path -Parent $Path
    if ($dir -and -not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [System.IO.File]::WriteAllText($Path, $Content, (New-Object System.Text.UTF8Encoding $false))
}

function Read-ProjectConfig {
    $path = Join-Path (Get-ExpeditionRoot) "project.config.json"
    if (-not (Test-Path $path)) { throw "Missing project.config.json at $path" }
    return Get-Content $path -Raw | ConvertFrom-Json
}

function Get-AdbSerial {
    param([object]$Config)
    if ($Config.devDevice.adbSerial) { return $Config.devDevice.adbSerial }
    $devices = & adb devices 2>$null | Select-Object -Skip 1 | Where-Object { $_ -match '\tdevice' }
    if ($devices.Count -eq 1) {
        return ($devices[0] -split '\t')[0]
    }
    return $null
}

function Invoke-BootstrapBash {
    param([string]$Script, [string[]]$Args = @())
    $root = Get-ExpeditionRoot
    Set-Location $root
    $bash = "bash"
    if (-not (Get-Command $bash -ErrorAction SilentlyContinue)) {
        $gitBash = "C:\Program Files\Git\bin\bash.exe"
        if (Test-Path $gitBash) { $bash = $gitBash } else { throw "bash required for $Script" }
    }
    $null = & $bash $Script @Args 2>&1
    if ($LASTEXITCODE) { return $LASTEXITCODE }
    if (-not $?) { return 1 }
    return 0
}

function Write-JsonResult {
    param([hashtable]$Result, [int]$ExitCode = 0)
    $Result | ConvertTo-Json -Compress | Write-Host
    exit $ExitCode
}

function Get-CursorAgentCli {
    if ($env:CURSOR_AGENT_BIN -and (Test-Path $env:CURSOR_AGENT_BIN)) {
        return $env:CURSOR_AGENT_BIN
    }
    $candidates = @(
        (Join-Path $env:USERPROFILE ".local\bin\agent.exe"),
        (Join-Path $env:USERPROFILE ".cursor\bin\agent.exe"),
        (Join-Path $env:LOCALAPPDATA "cursor-agent\agent.exe"),
        (Join-Path $env:LOCALAPPDATA "Programs\cursor-agent\agent.exe")
    )
    foreach ($path in $candidates) {
        if (-not (Test-Path $path)) { continue }
        $help = & $path --help 2>&1 | Out-String
        if ($help -match "worktree|Grok Build") { continue }
        if ($help -match "print|Cursor|ACP") { return $path }
    }
    $cmd = Get-Command agent -ErrorAction SilentlyContinue
    if ($cmd -and $cmd.Source -notmatch "\\.grok\\") {
        $help = & $cmd.Source --help 2>&1 | Out-String
        if ($help -match "worktree|--print|-p,") { return $cmd.Source }
    }
    return $null
}

function Get-ParallelManifest {
    param([string]$Sprint)
    $path = Join-Path (Get-ExpeditionRoot) "scripts/expedition/parallel-manifests/sprint-$Sprint.json"
    if (-not (Test-Path $path)) { throw "Missing parallel manifest: $path" }
    return Get-Content $path -Raw | ConvertFrom-Json
}

function Test-ParallelLockComplete {
    param(
        [string]$Sprint,
        [object]$Manifest
    )
    $planPath = Join-Path (Get-ExpeditionRoot) "BUILD_PLAN.md"
    $lines = Get-Content $planPath
    $inSprint = $false
    $step = 0
    $pattern = [regex]::Escape($Manifest.sequentialLockPattern)
    foreach ($line in $lines) {
        if ($line -match "^### Sprint $([regex]::Escape($Sprint)) —") { $inSprint = $true; continue }
        if ($inSprint -and $line -match "^### Sprint ") { break }
        if (-not $inSprint) { continue }
        if ($line -match '^(\d+)\.\s+(🔲|✅|❌|🔄)\s+\[AGENT\]') {
            $step = [int]$Matches[1]
            if ($step -eq $Manifest.sequentialLockStep) {
                return $Matches[2] -eq "✅"
            }
            if ($step -gt $Manifest.sequentialLockStep) { break }
        }
    }
    return $false
}
