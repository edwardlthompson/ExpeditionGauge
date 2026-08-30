# Sprint 32 leftover ADB smokes — crash review, HUD launch, inclinometer landscape.
function Invoke-Sprint32QualityScenario {
    param([string]$Name)
    switch ($Name) {
        "crash-review-smoke" { Invoke-CrashReviewSmoke; return $true }
        "emulator-hud-smoke" { Invoke-EmulatorHudSmoke; return $true }
        "inclinometer-landscape-pack" { Invoke-InclinometerLandscapePack; return $true }
        default { return $false }
    }
}

function Invoke-RunAsSh {
    param([string]$RemoteCmd)
    & adb -s $Serial shell "run-as $pkg sh -c '$RemoteCmd'"
}

function Write-RunAsFile {
    param([string]$RelPath, [string]$Content)
    $unix = $RelPath.Replace("\", "/")
    $dir = ($unix -replace "/[^/]+$", "")
    if ($dir -and $dir -ne $unix) {
        Invoke-RunAsSh "mkdir -p $dir" | Out-Null
    }
    $b64 = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($Content))
    Invoke-RunAsSh "echo $b64 | base64 -d > $unix" | Out-Null
}

function Test-RunAsFile {
    param([string]$RelPath)
    $unix = $RelPath.Replace("\", "/")
    $out = (Invoke-RunAsSh "ls $unix") -join "`n"
    return ($out -match [regex]::Escape(($unix -split "/")[-1]))
}

function Invoke-CrashReviewSmoke {
    Invoke-AdbCommand shell am force-stop $pkg | Out-Null
    Grant-RequiredPermissions
    $prefs = @"
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <boolean name="save_crashes" value="true" />
</map>
"@
    Write-RunAsFile -RelPath "shared_prefs/eg_feedback.xml" -Content $prefs
    Write-RunAsFile -RelPath "files/eg_pending_crash.txt" -Content "SmokeBoom`n---`njava.lang.IllegalStateException: adb smoke"
    Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
    Start-Sleep -Seconds 5
    Dismiss-OnboardingIfPresent
    $content = Get-UiDumpContent
    if ($content -notmatch "Review saved crash") {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "crash review dialog not shown after opt-in persist"
        } 1
    }
    $discard = Find-TapTarget -Content $content -Label "Discard"
    if (-not $discard) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Discard not found on crash review" } 1
    }
    Invoke-AdbCommand shell input tap $discard.X $discard.Y | Out-Null
    Start-Sleep -Seconds 2
    if (Test-RunAsFile -RelPath "files/eg_pending_crash.txt") {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "pending crash file still present after Discard" } 1
    }
}

function Invoke-EmulatorHudSmoke {
    Invoke-AdbCommand shell am force-stop $pkg | Out-Null
    Grant-RequiredPermissions
    Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
    Start-Sleep -Seconds 6
    Dismiss-OnboardingIfPresent
    $content = Get-UiDumpContent
    $hudVisible = (
        $content -match "Pitch:" -or
        $content -match "Roll:" -or
        $content -match "HDG" -or
        $content -match "Record"
    )
    if (-not $hudVisible) {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "HUD chrome not in UI dump after launch"
        } 1
    }
    Assert-ActivityVisible
    $shot = Join-Path $Root ".cursor\screenshots\phone-hud-smoke.png"
    New-Item -ItemType Directory -Force -Path (Split-Path $shot) | Out-Null
    $adbBin = (Get-Command adb).Source
    cmd /c "`"$adbBin`" -s $Serial exec-out screencap -p > `"$shot`""
}

function Close-NavDrawerIfOpen {
    param([string]$Content)
    if ($Content -match "Close navigation menu" -or $Content -match "Dashboard preset") {
        Invoke-AdbCommand shell input keyevent 4 | Out-Null
        Start-Sleep -Seconds 1
    }
}

function Select-OffroadInclinometer {
    $content = Get-UiDumpContent
    $menu = Find-TapTarget -Content $content -Label "Open menu"
    if (-not $menu) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Open menu not found" } 1
    }
    Invoke-AdbCommand shell input tap $menu.X $menu.Y | Out-Null
    Start-Sleep -Seconds 2
    $content = Get-UiDumpContent
    $presetPage = Find-TapTarget -Content $content -Label "Dashboard preset"
    if ($presetPage) {
        Invoke-AdbCommand shell input tap $presetPage.X $presetPage.Y | Out-Null
        Start-Sleep -Seconds 2
        $content = Get-UiDumpContent
    }
    $offroad = Find-TapTarget -Content $content -Label "Offroad"
    if (-not $offroad) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Offroad preset not found" } 1
    }
    Invoke-AdbCommand shell input tap $offroad.X $offroad.Y | Out-Null
    Start-Sleep -Seconds 2
    $content = Get-UiDumpContent
    Close-NavDrawerIfOpen -Content $content
    Start-Sleep -Seconds 1
    return Get-UiDumpContent
}

function Test-InclinometerVisible {
    param([string]$Content)
    return (
        $Content -match "Inclinometer" -or
        $Content -match "inclinometer_gauge"
    )
}

function Invoke-InclinometerLandscapePack {
    Restore-DeviceRotation
    try {
        Invoke-AdbCommand shell am force-stop $pkg | Out-Null
        Grant-RequiredPermissions
        Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
        Start-Sleep -Seconds 6
        Dismiss-OnboardingIfPresent
        $content = Select-OffroadInclinometer
        if (-not (Test-InclinometerVisible -Content $content)) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "inclinometer not visible after Offroad" } 1
        }
        Set-DeviceRotation -Rotation 1
        Start-Sleep -Seconds 4
        $content = Get-UiDumpContent
        Close-NavDrawerIfOpen -Content $content
        $content = Get-UiDumpContent
        if (-not (Test-InclinometerVisible -Content $content)) {
            Write-JsonResult @{
                status = "fail"
                scenario = $Scenario
                reason = "inclinometer lost after landscape rotation (ADR-0013)"
            } 1
        }
        Assert-ActivityVisible
    } finally {
        Restore-DeviceRotation
    }
}
