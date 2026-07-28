#Requires -Version 5.1
<#
.SYNOPSIS
  Start Desktop Head Unit with a named-pipe stdin bridge for scripted taps.

.DESCRIPTION
  Google documents `./desktop-head-unit < script.txt` for CI. This keeps DHU
  running and forwards lines from \\.\pipe\ExpeditionGaugeDhu → DHU stdin so
  other scripts can `dhu-console.ps1 -Command "tap x y"` without mouse_event.

.EXAMPLE
  pwsh scripts/expedition/dhu-start-controlled.ps1 -RestartDhu
#>
param(
    [string]$AdbPort = "5277",
    [switch]$RestartDhu,
    [string[]]$DhuExtraArgs = @(),
    [string]$PipeName = "ExpeditionGaugeDhu"
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"

$dhu = Join-Path $env:LOCALAPPDATA "Android\Sdk\extras\google\auto\desktop-head-unit.exe"
if (-not (Test-Path $dhu)) {
    Write-Error "dhu-start-controlled: DHU missing at $dhu"
}

$marker = Join-Path $env:TEMP "expeditiongauge-dhu-controlled.pid"
if ($RestartDhu) {
    Get-Process -Name "desktop-head-unit" -ErrorAction SilentlyContinue | Stop-Process -Force
    Start-Sleep -Seconds 1
    if (Test-Path $marker) { Remove-Item -Force $marker }
}

if (Get-Process -Name "desktop-head-unit" -ErrorAction SilentlyContinue) {
    if (Test-Path $marker) {
        Write-Host "OK  DHU already controlled (pid marker present)" -ForegroundColor DarkGray
        exit 0
    }
    Write-Warning "DHU running without pipe bridge — use -RestartDhu to enable console taps"
    exit 2
}

$argList = @("-a", $AdbPort, "-i", "touch") + $DhuExtraArgs
$workDir = Split-Path $dhu
$bridge = @"
`$ErrorActionPreference = 'Stop'
`$dhu = '$dhu'
`$workDir = '$workDir'
`$args = @($(($argList | ForEach-Object { "'$_'" }) -join ','))
`$pipeName = '$PipeName'
`$psi = New-Object System.Diagnostics.ProcessStartInfo
`$psi.FileName = `$dhu
`$psi.Arguments = (`$args -join ' ')
`$psi.WorkingDirectory = `$workDir
`$psi.UseShellExecute = `$false
`$psi.RedirectStandardInput = `$true
`$psi.CreateNoWindow = `$false
`$p = [System.Diagnostics.Process]::Start(`$psi)
[System.IO.File]::WriteAllText('$marker', `$p.Id.ToString())
try {
  while (-not `$p.HasExited) {
    `$server = New-Object System.IO.Pipes.NamedPipeServerStream(`$pipeName, [System.IO.Pipes.PipeDirection]::In)
    `$server.WaitForConnection()
    `$reader = New-Object System.IO.StreamReader(`$server)
    while ((`$line = `$reader.ReadLine()) -ne `$null) {
      if (`$line.Trim().Length -eq 0) { continue }
      `$p.StandardInput.WriteLine(`$line)
      `$p.StandardInput.Flush()
    }
    `$reader.Dispose()
    `$server.Dispose()
  }
} finally {
  if (Test-Path '$marker') { Remove-Item -Force '$marker' -ErrorAction SilentlyContinue }
}
"@

$bridgePath = Join-Path $env:TEMP "expeditiongauge-dhu-bridge.ps1"
# UTF-8 no BOM
[System.IO.File]::WriteAllText($bridgePath, $bridge, (New-Object System.Text.UTF8Encoding $false))
Start-Process -FilePath "powershell.exe" -ArgumentList @(
    "-NoProfile", "-WindowStyle", "Hidden", "-File", $bridgePath
) -WindowStyle Hidden | Out-Null

$deadline = (Get-Date).AddSeconds(15)
while ((Get-Date) -lt $deadline) {
    if ((Get-Process -Name "desktop-head-unit" -ErrorAction SilentlyContinue) -and (Test-Path $marker)) {
        Write-Host "OK  Controlled DHU started (pipe \\.\pipe\$PipeName)" -ForegroundColor Green
        exit 0
    }
    Start-Sleep -Milliseconds 400
}
Write-Error "dhu-start-controlled: DHU did not start with pipe bridge in time"
