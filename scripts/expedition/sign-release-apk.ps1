# Sign reproducible unsigned release APK for sideload (debug keystore).
param(
    [string]$UnsignedApk = "",
    [string]$OutApk = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

if (-not $UnsignedApk) {
    $UnsignedApk = Join-Path $Root "examples\android\app\build\outputs\apk\release\app-release-unsigned.apk"
}
if (-not (Test-Path $UnsignedApk)) {
    Write-Error "sign-release-apk: unsigned APK not found at $UnsignedApk (run assembleRelease first)"
}

if (-not $OutApk) {
    $ver = "0.0.0"
    $gradle = Join-Path $Root "examples\android\app\build.gradle.kts"
    if (Test-Path $gradle) {
        $raw = Get-Content $gradle -Raw
        if ($raw -match 'versionName\s*=\s*"([^"]+)"') { $ver = $Matches[1] }
    }
    $OutApk = Join-Path $Root "ExpeditionGauge-$ver.apk"
}

$ks = Join-Path $env:USERPROFILE ".android\debug.keystore"
if (-not (Test-Path $ks)) {
    Write-Error "sign-release-apk: debug keystore missing at $ks"
}

$sdk = $env:ANDROID_HOME
if (-not $sdk) { $sdk = $env:ANDROID_SDK_ROOT }
if (-not $sdk) {
    Write-Error "sign-release-apk: set ANDROID_HOME or ANDROID_SDK_ROOT"
}

$buildTools = Get-ChildItem (Join-Path $sdk "build-tools") -Directory | Sort-Object Name -Descending | Select-Object -First 1
$apksigner = Join-Path $buildTools.FullName "apksigner.bat"
if (-not (Test-Path $apksigner)) {
    Write-Error "sign-release-apk: apksigner not found under $sdk\build-tools"
}

$staging = [System.IO.Path]::GetTempFileName() + ".apk"
Copy-Item $UnsignedApk $staging -Force
& $apksigner sign --ks $ks --ks-pass pass:android --key-pass pass:android --out $OutApk $staging
Remove-Item $staging -Force
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Write-Host "Signed $OutApk" -ForegroundColor Green
