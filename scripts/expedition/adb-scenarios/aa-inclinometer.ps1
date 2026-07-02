function Invoke-AdbInclinometerScenario {
    Invoke-AdbCommand shell input keyevent KEYCODE_WAKEUP | Out-Null
    Invoke-AdbCommand shell am force-stop $pkg | Out-Null
    Grant-RequiredPermissions
    Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
    Start-Sleep -Seconds 6
    Dismiss-OnboardingIfPresent
    Start-Sleep -Seconds 2

    $content = Get-UiDumpContent
    $hudVisible = (
        $content -match "Pitch:" -or
        $content -match "Roll:" -or
        $content -match "HDG" -or
        $content -match "Record" -or
        $content -match "resource-id=`"record_play`""
    )
    if (-not $hudVisible) {
        Assert-ActivityVisible
        Write-JsonResult @{
            status = "blocker"
            scenario = $Scenario
            reason = "dashboard HUD labels not in UI dump — unlock phone, dismiss onboarding, retry; use manual DHU checklist in docs/help/ANDROID_AUTO.md"
        } 2
    }

    $offroad = Find-TapTarget -Content $content -Label "Offroad"
    if (-not $offroad) {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "Offroad preset chip not found"
        } 1
    }
    Invoke-AdbCommand shell input tap $offroad.X $offroad.Y | Out-Null
    Start-Sleep -Seconds 2
    $content = Get-UiDumpContent

    if ($content -notmatch "CRAWL") {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "Offroad preset did not activate CRAWL recording mode badge"
        } 1
    }

    if ($content -notmatch "Inclinometer") {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "inclinometer gauge semantics not found after Offroad preset"
        } 1
    }

    $gauge = Find-TapTarget -Content $content -Label "Inclinometer"
    if ($gauge) {
        Invoke-AdbCommand shell input tap $gauge.X $gauge.Y | Out-Null
        Start-Sleep -Seconds 2
        $sheet = Get-UiDumpContent
        if ($sheet -notmatch "Calibrate / Set Level" -and $sheet -notmatch "inclinometer_calibrate") {
            Write-JsonResult @{
                status = "fail"
                scenario = $Scenario
                reason = "inclinometer calibrate sheet not opened"
            } 1
        }
        Invoke-AdbCommand shell input keyevent 4 | Out-Null
        Start-Sleep -Seconds 1
    }

    $dump = (Invoke-AdbCommand shell dumpsys package $pkg) -join "`n"
    if ($dump -notmatch "ExpeditionGaugeCarAppService") {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "ExpeditionGaugeCarAppService not registered"
        } 1
    }

    $carHost = (Invoke-AdbCommand shell dumpsys activity services) -join "`n"
    $aaConnected = $carHost -match "car\.app|Gearhead|Android Auto"
    if (-not $aaConnected) {
        Write-JsonResult @{
            status = "ok"
            scenario = $Scenario
            serial = $Serial
            package = $pkg
            note = "phone inclinometer checks passed; AA head unit not connected — complete manual DHU checklist in docs/help/ANDROID_AUTO.md"
            aa_host = "disconnected"
        }
        return
    }

    Write-JsonResult @{
        status = "ok"
        scenario = $Scenario
        serial = $Serial
        package = $pkg
        note = "phone inclinometer checks passed; AA host connected — verify Attitude tile, Zero, Record/Stop on head unit manually"
        aa_host = "connected"
    }
}
