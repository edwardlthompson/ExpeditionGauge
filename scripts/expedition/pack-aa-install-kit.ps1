# Build ExpeditionGauge-X.Y.Z-AA-install-kit.zip for GitHub Releases.
# Contents: signed APK + Play Store spoof install scripts + run-as-uid helper + guide.
param(
    [string]$Apk = "",
    [string]$OutDir = ""
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

if (-not $Apk) {
    $Apk = Get-ChildItem (Join-Path $Root "ExpeditionGauge-*.apk") -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch "AA-install" } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}
if (-not $Apk -or -not (Test-Path $Apk)) {
    Write-Error "pack-aa-install-kit: pass -Apk path to ExpeditionGauge-X.Y.Z.apk"
}

$apkLeaf = Split-Path $Apk -Leaf
if ($apkLeaf -notmatch '^ExpeditionGauge-(.+)\.apk$') {
    Write-Error "pack-aa-install-kit: APK name must be ExpeditionGauge-X.Y.Z.apk (got $apkLeaf)"
}
$ver = $Matches[1]
if (-not $OutDir) { $OutDir = $Root }

$staging = Join-Path $env:TEMP "eg-aa-kit-$ver"
if (Test-Path $staging) { Remove-Item $staging -Recurse -Force }
New-Item -ItemType Directory -Path (Join-Path $staging "bin") | Out-Null

Copy-Item $Apk (Join-Path $staging $apkLeaf)
Copy-Item (Join-Path $PSScriptRoot "install-aa-from-pc.ps1") $staging
Copy-Item (Join-Path $PSScriptRoot "install-aa-from-pc.sh") $staging
Copy-Item (Join-Path $PSScriptRoot "aa-spoof-adb.sh") $staging
Copy-Item (Join-Path $PSScriptRoot "bin\run-as-uid-arm64") (Join-Path $staging "bin\run-as-uid-arm64")
Copy-Item (Join-Path $Root "docs\help\ANDROID_AUTO_SIDELOAD.md") (Join-Path $staging "ANDROID_AUTO_SIDELOAD.md")

$guide = @"
ExpeditionGauge $ver — Android Auto Play Store spoof install kit
================================================================

Why this kit exists
-------------------
Plain ``adb install`` / browser install leaves initiatingPackageName=com.android.shell.
Android Auto Customize launcher then hides the app. This kit creates the install
session as the Play Store UID so BOTH installer and initiator are com.android.vending.

Requirements
------------
- USB debugging enabled; phone unlocked and authorized
- Rooted phone: Magisk ``su`` OR ``adb root`` (helper bin/run-as-uid-arm64 included)
- adb on PATH (Android platform-tools)

Quick install (recommended)
---------------------------
PowerShell:

  pwsh .\install-aa-from-pc.ps1 -Apk .\$apkLeaf

Bash / Git Bash / WSL / macOS / Linux:

  bash ./aa-spoof-adb.sh $apkLeaf

Verify
------
  adb shell dumpsys package dev.foss.expeditiongauge | findstr /i "installerPackageName initiatingPackageName"

Both lines must be com.android.vending.

Then on the phone
-----------------
1. Android Auto → About → tap version ~10x → Developer settings → Unknown sources ON
2. Customize launcher → enable ExpeditionGauge
3. USB reconnect to the head unit

Full guide: ANDROID_AUTO_SIDELOAD.md
https://github.com/edwardlthompson/ExpeditionGauge/blob/main/docs/help/ANDROID_AUTO_SIDELOAD.md
"@
[System.IO.File]::WriteAllText((Join-Path $staging "README.txt"), $guide, (New-Object System.Text.UTF8Encoding $false))

$zip = Join-Path $OutDir "ExpeditionGauge-$ver-AA-install-kit.zip"
if (Test-Path $zip) { Remove-Item $zip -Force }

# Compress-Archive paths are relative to staging root for a clean zip layout.
Push-Location $staging
try {
    Compress-Archive -Path * -DestinationPath $zip -Force
}
finally {
    Pop-Location
}

Remove-Item $staging -Recurse -Force
Write-Host "Wrote $zip" -ForegroundColor Green
Write-Output $zip
