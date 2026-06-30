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
    & $bash $Script @Args
    return $LASTEXITCODE
}

function Write-JsonResult {
    param([hashtable]$Result, [int]$ExitCode = 0)
    $Result | ConvertTo-Json -Compress | Write-Host
    exit $ExitCode
}
