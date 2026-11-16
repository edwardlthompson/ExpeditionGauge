# Cross-compile scripts/expedition/bin/run-as-uid-arm64 with the Android NDK.
param([switch]$Force)
$ErrorActionPreference = "Stop"
$Here = $PSScriptRoot
$Src = Join-Path $Here "bin\run-as-uid.c"
$Out = Join-Path $Here "bin\run-as-uid-arm64"
if (-not (Test-Path $Src)) { Write-Error "build-run-as-uid: missing $Src" }
if (-not $Force -and (Test-Path $Out) -and ((Get-Item $Out).LastWriteTime -gt (Get-Item $Src).LastWriteTime)) {
    Write-Host "OK   $Out (up to date)"
    exit 0
}

$ndk = $env:ANDROID_NDK_HOME
if (-not $ndk) { $ndk = $env:ANDROID_NDK_ROOT }
if (-not $ndk -or -not (Test-Path $ndk)) {
    foreach ($sdk in @($env:ANDROID_SDK_ROOT, $env:ANDROID_HOME)) {
        if (-not $sdk) { continue }
        $ndkRoot = Join-Path $sdk "ndk"
        if (Test-Path $ndkRoot) {
            $ndk = Get-ChildItem $ndkRoot -Directory | Sort-Object Name | Select-Object -Last 1 -ExpandProperty FullName
            if ($ndk) { break }
        }
    }
}
if (-not $ndk -or -not (Test-Path $ndk)) {
    Write-Error "build-run-as-uid: set ANDROID_NDK_HOME or install an NDK under ANDROID_SDK_ROOT/ndk"
}

$hostTag = if ($IsWindows -or $env:OS -match "Windows") { "windows-x86_64" } elseif ($IsMacOS) {
    if ((uname -m) -eq "arm64") { "darwin-arm64" } else { "darwin-x86_64" }
} else { "linux-x86_64" }
$clang = Join-Path $ndk "toolchains\llvm\prebuilt\$hostTag\bin\aarch64-linux-android21-clang"
if ($hostTag -eq "windows-x86_64" -and -not (Test-Path $clang)) { $clang = "$clang.cmd" }
if (-not (Test-Path $clang)) { Write-Error "build-run-as-uid: NDK clang not found at $clang" }

New-Item -ItemType Directory -Force -Path (Join-Path $Here "bin") | Out-Null
& $clang -O2 -fPIE -pie -s -o $Out $Src
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "OK   built $Out" -ForegroundColor Green
