# Pre-release gate + gh release create.
param(
    [string]$Tag = "",
    [switch]$Draft
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
$draftFlag = if ($Draft -or $config.releaseDraft) { "--draft" } else { "" }

$notes = Join-Path $Root "RELEASE_NOTES.md"
$args = @("release", "create", $Tag, "--title", $Tag)
if ($draftFlag) { $args += "--draft" }
if (Test-Path $notes) { $args += @("--notes-file", $notes) }

gh @args
exit $LASTEXITCODE
