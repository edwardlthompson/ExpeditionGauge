# Pre-release gate (PowerShell wrapper).
# Prefer agent-run so JAVA_HOME / gh PATH survive on Windows (KB-025).
param()

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $Root

$py = Get-Command python3 -ErrorAction SilentlyContinue
if (-not $py) { $py = Get-Command python -ErrorAction SilentlyContinue }
if ($py) {
    & $py.Source scripts/agent-run.py pre-release-gate
    exit $LASTEXITCODE
}

$Bash = "bash"
if (-not (Get-Command $Bash -ErrorAction SilentlyContinue)) {
    if (Test-Path "C:\Program Files\Git\bin\bash.exe") {
        $Bash = "C:\Program Files\Git\bin\bash.exe"
    } else {
        Write-Host "ERROR: python3/python or bash (Git for Windows) required"
        exit 1
    }
}

# Fallback: export JAVA_HOME + common tool dirs into the bash child (KB-025).
$pathPrefix = @()
if ($env:JAVA_HOME) {
    $pathPrefix += (Join-Path $env:JAVA_HOME "bin")
}
foreach ($dir in @(
        "${env:ProgramFiles}\GitHub CLI",
        "${env:LOCALAPPDATA}\Android\Sdk\platform-tools"
    )) {
    if ($dir -and (Test-Path $dir)) { $pathPrefix += $dir }
}
$prefixColon = ($pathPrefix -join ":")
$jh = $env:JAVA_HOME
$ah = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { $env:ANDROID_SDK_ROOT }
$bashCmd = @"
set -e
$(if ($jh) { "export JAVA_HOME=`"$jh`"" })
$(if ($ah) { "export ANDROID_HOME=`"$ah`"; export ANDROID_SDK_ROOT=`"$ah`"" })
$(if ($prefixColon) { "export PATH=`"$prefixColon:`$PATH`"" })
cd `"$Root`"
scripts/pre-release-gate.sh
"@
& $Bash -lc $bashCmd
exit $LASTEXITCODE
