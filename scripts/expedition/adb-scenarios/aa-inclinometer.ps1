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

    # Presets live in the hamburger drawer (Dashboard HUD v2), not as top-bar chips.
    $menu = Find-TapTarget -Content $content -Label "Open menu"
    if (-not $menu) {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "Open menu control not found"
        } 1
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
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "Offroad preset not found in dashboard menu"
        } 1
    }
    Invoke-AdbCommand shell input tap $offroad.X $offroad.Y | Out-Null
    Start-Sleep -Seconds 2
    $content = Get-UiDumpContent

    # Offroad switches attitude to inclinometer immediately; CRAWL badge only while recording.
    if ($content -notmatch "Inclinometer") {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "inclinometer gauge semantics not found after Offroad preset"
        } 1
    }

    $record = Find-TapTarget -Content $content -Label "Record"
    if ($record) {
        Invoke-AdbCommand shell input tap $record.X $record.Y | Out-Null
        Start-Sleep -Seconds 2
        $recordingUi = Get-UiDumpContent
        if ($recordingUi -notmatch "CRAWL") {
            Write-JsonResult @{
                status = "fail"
                scenario = $Scenario
                reason = "Offroad preset did not show CRAWL badge while recording"
            } 1
        }
        $stop = Find-TapTarget -Content $recordingUi -Label "Stop"
        if ($stop) {
            Invoke-AdbCommand shell input tap $stop.X $stop.Y | Out-Null
            Start-Sleep -Seconds 1
        }
        $content = Get-UiDumpContent
    }

    # content-desc is "Inclinometer pitch …, roll …" — long-press opens calibrate sheet.
    if ($content -match 'content-desc="Inclinometer[^"]*"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
        $gx = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
        $gy = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
        # Long-press (~1s) opens calibrate; tap/swipe toggles G-meter ↔ inclinometer.
        Invoke-AdbCommand shell input swipe $gx $gy $gx $gy 1000 | Out-Null
        Start-Sleep -Seconds 2
        $sheet = Get-UiDumpContent
        if ($sheet -notmatch "Calibrate / Set Level" -and $sheet -notmatch "inclinometer_calibrate") {
            Write-JsonResult @{
                status = "fail"
                scenario = $Scenario
                reason = "inclinometer calibrate sheet not opened on long-press"
            } 1
        }
        Invoke-AdbCommand shell input keyevent 4 | Out-Null
        Start-Sleep -Seconds 1
        # Tap toggles to G-meter (ATTITUDE); inclinometer content-desc should disappear.
        Invoke-AdbCommand shell input tap $gx $gy | Out-Null
        Start-Sleep -Seconds 1
        $toggled = Get-UiDumpContent
        if ($toggled -match 'content-desc="Inclinometer') {
            Write-JsonResult @{
                status = "fail"
                scenario = $Scenario
                reason = "tap did not toggle inclinometer to G-meter"
            } 1
        }
    }

    $dump = (Invoke-AdbCommand shell dumpsys package $pkg) -join "`n"
    if ($dump -notmatch "ExpeditionGaugeCarAppService") {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "ExpeditionGaugeCarAppService not registered"
        } 1
    }
    if ($dump -notmatch "androidx\.car\.app\.category\.POI") {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "CarAppService missing androidx.car.app.category.POI (required for AA launcher discovery)"
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
