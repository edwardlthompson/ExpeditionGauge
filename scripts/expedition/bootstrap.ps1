# ExpeditionGauge bootstrap wrapper.
param(
    [switch]$Init,
    [switch]$CreateRepo
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
Set-Location $Root

$config = Read-ProjectConfig

if ($CreateRepo) {
    & "$PSScriptRoot\ensure-gh-auth.ps1"
    if ($LASTEXITCODE -eq 2) { exit 2 }
}

& "$PSScriptRoot\materialize-build-plan.ps1"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$initArgs = @(
    "-Stack", "android",
    "-ProjectName", $config.projectName,
    "-ProjectPurpose", $config.purpose,
    "-Prune",
    "-NonInteractive"
)
if ($config.releaseRepo -and $config.releaseRepo -notmatch 'OWNER') {
    $initArgs += @("-ReleaseRepo", $config.releaseRepo)
}
if ($config.donationsUrl) {
    $initArgs += @("-DonationUrl", $config.donationsUrl)
}
if ($config.maintainer) {
    $initArgs += @("-CodeOwner", $config.maintainer)
}

& (Join-Path $Root "scripts\init-project.ps1") @initArgs
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

if ($CreateRepo) {
    $repo = $config.releaseRepo -replace '^github\.com/', ''
    if ($repo -and $repo -notmatch 'OWNER') {
        & (Join-Path $Root "scripts\setup-github-repo.ps1") -Repo $repo
    }
}

Write-Host "bootstrap: OK" -ForegroundColor Green
