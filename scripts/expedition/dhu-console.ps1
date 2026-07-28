#Requires -Version 5.1
<#
.SYNOPSIS
  Send Desktop Head Unit console commands (no mouse taps).

.DESCRIPTION
  DHU accepts stdin commands (tap/sleep/screenshot/…). Our controlled launcher
  bridges a named pipe to that stdin. Prefer this over Windows mouse_event.

.EXAMPLE
  pwsh scripts/expedition/dhu-console.ps1 -Command "tap 48 463"
  pwsh scripts/expedition/dhu-console.ps1 -Command "sleep 2; tap 480 230"
#>
param(
    [Parameter(Mandatory = $true)]
    [string]$Command,
    [string]$PipeName = "ExpeditionGaugeDhu"
)

$ErrorActionPreference = "Stop"
$full = "\\.\pipe\$PipeName"
try {
    $client = New-Object System.IO.Pipes.NamedPipeClientStream(".", $PipeName, [System.IO.Pipes.PipeDirection]::Out)
    $client.Connect(2000)
    $writer = New-Object System.IO.StreamWriter($client)
    $writer.AutoFlush = $true
    foreach ($part in ($Command -split ';')) {
        $line = $part.Trim()
        if ($line) { $writer.WriteLine($line) }
    }
    $writer.Dispose()
    $client.Dispose()
    Write-Host "OK  dhu-console: $Command" -ForegroundColor DarkGray
} catch {
    Write-Error "dhu-console: pipe '$full' not available — start DHU via dhu-smoke/dhu-start-controlled (not a bare Start-Process). $_"
}
