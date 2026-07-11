# Pre-release gate + assemble/sign APK + gh release create (with APK asset).
param(
    [string]$Tag = "",
    [switch]$Draft,
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
Set-Location $Root

$config = Read-ProjectConfig
& (Join-Path $Root "scripts\pre-release-gate.ps1")
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

& "$PSScriptRoot\ensure-gh-auth.ps1"
if ($LASTEXITCODE -eq 2) { exit 2 }

if (-not $Tag) {
    $gradlePath = Join-Path $Root "examples\android\app\build.gradle.kts"
    if (Test-Path $gradlePath) {
        $gradle = Get-Content $gradlePath -Raw
        if ($gradle -match 'versionName\s*=\s*"([^"]+)"') {
            $Tag = "v$($Matches[1])"
        }
    }
    if (-not $Tag) { $Tag = "v0.1.0" }
}

$ver = $Tag.TrimStart("v")
$apk = Join-Path $Root "ExpeditionGauge-$ver.apk"

if (-not $SkipBuild) {
    $androidDir = Join-Path $Root "examples\android"
    if (-not (Test-Path (Join-Path $androidDir "gradlew.bat"))) {
        Write-Error "create-release: missing examples/android/gradlew.bat"
    }
    $prevEpoch = $env:SOURCE_DATE_EPOCH
    $env:SOURCE_DATE_EPOCH = "1700000000"
    Push-Location $androidDir
    try {
        & .\gradlew.bat assembleRelease --no-daemon
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    }
    finally {
        Pop-Location
        if ($null -eq $prevEpoch) { Remove-Item Env:SOURCE_DATE_EPOCH -ErrorAction SilentlyContinue }
        else { $env:SOURCE_DATE_EPOCH = $prevEpoch }
    }
    & "$PSScriptRoot\sign-release-apk.ps1"
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

if (-not (Test-Path $apk)) {
    Write-Error "create-release: signed APK missing at $apk (run assembleRelease + sign-release-apk.ps1)"
}

$draftFlag = if ($Draft -or $config.releaseDraft) { "--draft" } else { "" }

$notes = Join-Path $Root "RELEASE_NOTES.md"
$ghArgs = @("release", "create", $Tag, "--title", $Tag, $apk)
if ($draftFlag) { $ghArgs += "--draft" }
if (Test-Path $notes) { $ghArgs += @("--notes-file", $notes) }

gh @ghArgs
exit $LASTEXITCODE
