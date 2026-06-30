# GitHub CLI auth gate — exit 2 when gh not authenticated (documented blocker).
param()

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"

if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
    Write-Host '{"status":"blocker","reason":"gh CLI not installed"}'
    Write-Host "Install GitHub CLI: https://cli.github.com/"
    exit 2
}

$auth = gh auth status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host '{"status":"blocker","reason":"gh not authenticated"}'
    Write-Host "Run: gh auth login"
    exit 2
}

Write-Host '{"status":"ok","reason":"gh authenticated"}'
exit 0
