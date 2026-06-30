# Flip task emoji markers in BUILD_PLAN.md.
param(
    [Parameter(Mandatory = $true)]
    [string]$Pattern,
    [ValidateSet("done", "pending", "progress")]
    [string]$Status = "done"
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$planPath = Join-Path $Root "BUILD_PLAN.md"
$content = Get-Content $planPath -Raw

$from = switch ($Status) {
    "done" { "🔲"; "✅" }
    "pending" { "✅"; "🔲" }
    "progress" { "🔲"; "🔄" }
}

if ($content -notmatch [regex]::Escape($Pattern)) {
    Write-Error "Pattern not found in BUILD_PLAN.md: $Pattern"
    exit 1
}

$escaped = [regex]::Escape($Pattern)
$content = [regex]::Replace($content, "🔲 \[$escaped\]", "✅ [$Pattern]", 1)
if ($content -notmatch "✅ \[$([regex]::Escape($Pattern))\]") {
    $content = [regex]::Replace($content, $escaped, $Pattern, 1)
    $content = $content -replace "🔲 \[AGENT\].*$([regex]::Escape($Pattern.Substring(0, [Math]::Min(40, $Pattern.Length))))", "✅ [AGENT] $Pattern"
}

Write-Utf8NoBom $planPath $content
Write-Host "mark-task: updated pattern matching '$Pattern'"
