# Shared ADB smoke helpers — dot-source from adb-smoke.ps1 after `$pkg` and `$Scenario` are set.

function Invoke-AdbCommand {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$AdbArgs)
    if ($Serial) {
        & adb -s $Serial @AdbArgs
    } else {
        & adb @AdbArgs
    }
    return $LASTEXITCODE
}

function Get-UiDumpContent {
    Invoke-AdbCommand shell uiautomator dump /sdcard/window_dump.xml | Out-Null
    $dumpFile = Join-Path $env:TEMP "expedition-ui-dump.xml"
    Invoke-AdbCommand pull /sdcard/window_dump.xml $dumpFile | Out-Null
    return Get-Content $dumpFile -Raw
}

function Find-TapTarget {
    param([string]$Content, [string]$Label)
    $escaped = [regex]::Escape($Label)
    $patterns = @(
        "text=`"$escaped`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`""
        "bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`"[^>]*text=`"$escaped`""
        "content-desc=`"$escaped`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`""
        "bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`"[^>]*content-desc=`"$escaped`""
    )
    foreach ($pattern in $patterns) {
        $matches = [regex]::Matches($Content, $pattern)
        foreach ($match in $matches) {
            $left = [int]$match.Groups[1].Value
            $top = [int]$match.Groups[2].Value
            $right = [int]$match.Groups[3].Value
            $bottom = [int]$match.Groups[4].Value
            if ($right -le $left -or $bottom -le $top) { continue }
            return @{
                X = [int](($left + $right) / 2)
                Y = [int](($top + $bottom) / 2)
            }
        }
    }
    return $null
}

function Find-SessionCard {
    param([string]$Content)
    if ($Content -match 'text="(20\d{2}-\d{2}-\d{2}[^"]*)"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
        return @{
            X = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
            Y = [int](([int]$Matches[3] + [int]$Matches[5]) / 2)
        }
    }
    if ($Content -match 'clickable="true"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"[^>]*><node[^>]*text="20\d{2}-') {
        return @{
            X = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
            Y = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
        }
    }
    return $null
}

function Test-PlaybackScreenOpen {
    param([string]$Content)
    return (
        $Content -match 'text="Playback"' -or
        $Content -match 'playback index \d+ of \d+' -or
        $Content -match 'playback layout map weight'
    )
}

function Open-PlaybackScreen {
    Invoke-AdbCommand shell am force-stop $pkg | Out-Null
    Grant-RequiredPermissions
    Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
    Start-Sleep -Seconds 3
    Dismiss-OnboardingIfPresent
    $content = Get-UiDumpContent
    $sessionsBtn = Find-TapTarget -Content $content -Label "Sessions"
    if (-not $sessionsBtn) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Sessions button not found" } 1
    }
    Invoke-AdbCommand shell input tap $sessionsBtn.X $sessionsBtn.Y | Out-Null
    Start-Sleep -Seconds 3
    $play = $null
    foreach ($unused in 1..5) {
        $content = Get-UiDumpContent
        $play = Find-TapTarget -Content $content -Label "Play"
        if ($play) { break }
        if (-not (Find-SessionCard -Content $content)) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "no recorded session in list" } 1
        }
        Start-Sleep -Seconds 1
    }
    if (-not $play) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Play button not found on session list" } 1
    }
    Invoke-AdbCommand shell input tap $play.X $play.Y | Out-Null
    Start-Sleep -Seconds 6
    $content = Get-UiDumpContent
    if (-not (Test-PlaybackScreenOpen -Content $content)) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Playback screen not opened" } 1
    }
    return $content
}

function Invoke-DriftAnalysisToggle {
    param([string]$Content)
    if ($Content -match 'content-desc="Drift Analysis"[\s\S]*?checkable="true" checked="(?:true|false)" clickable="true"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
        $x = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
        $y = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
        Invoke-AdbCommand shell input tap $x $y | Out-Null
        return $true
    }
    return $false
}

function Assert-ActivityVisible {
    $dump = Invoke-AdbCommand shell dumpsys activity activities 2>$null | Out-String
    if ($dump -notmatch [regex]::Escape($pkg)) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "activity not visible" } 1
    }
}

function Get-PlaybackIndexFromDump {
    param([string]$Content)
    if ($Content -match 'content-desc="playback index (\d+) of (\d+)"') {
        return [int]$Matches[1]
    }
    if ($Content -match 'text="Sample (\d+) / (\d+)"') {
        return [int]$Matches[1]
    }
    if ($Content -match 'text="(\d+) / (\d+)"') {
        return [int]$Matches[1]
    }
    return $null
}

function Invoke-PlaybackKey {
    param([int]$KeyCode)
    Invoke-AdbCommand shell input keyevent $KeyCode | Out-Null
    Start-Sleep -Milliseconds 350
}

function Set-DeviceRotation {
    param([int]$Rotation)
    Invoke-AdbCommand shell settings put system accelerometer_rotation 0 | Out-Null
    Invoke-AdbCommand shell settings put system user_rotation $Rotation | Out-Null
    Start-Sleep -Seconds 2
}

function Restore-DeviceRotation {
    Invoke-AdbCommand shell settings put system user_rotation 0 | Out-Null
    Invoke-AdbCommand shell settings put system accelerometer_rotation 1 | Out-Null
    Start-Sleep -Seconds 1
}

function Get-TapTargetBounds {
    param([string]$Content, [string]$Label)
    $escaped = [regex]::Escape($Label)
    $patterns = @(
        "text=`"$escaped`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`""
        "bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`"[^>]*text=`"$escaped`""
        "content-desc=`"$escaped`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`""
        "bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]`"[^>]*content-desc=`"$escaped`""
    )
    foreach ($pattern in $patterns) {
        if ($Content -match $pattern) {
            return @{
                Left = [int]$Matches[1]
                Top = [int]$Matches[2]
                Right = [int]$Matches[3]
                Bottom = [int]$Matches[4]
            }
        }
    }
    return $null
}

function Get-ScreenHeightPx {
    $out = (Invoke-AdbCommand shell wm size 2>$null | Out-String)
    if ($out -match '(\d+)x(\d+)') {
        return [int]$Matches[2]
    }
    return 2400
}

function Get-NavBarReservePx {
    param([ValidateSet("three-button", "gesture")][string]$Mode)
    $densityOut = (Invoke-AdbCommand shell wm density 2>$null | Out-String)
    $density = 420
    if ($densityOut -match '(\d+)') { $density = [int]$Matches[1] }
    $dp = if ($Mode -eq "gesture") { 48 } else { 96 }
    return [int](($dp * $density) / 160)
}

function Set-NavigationMode {
    param([ValidateSet("three-button", "gesture")][string]$Mode)
    $value = if ($Mode -eq "gesture") { 2 } else { 0 }
    Invoke-AdbCommand shell settings put secure navigation_mode $value | Out-Null
    Start-Sleep -Seconds 2
}

function Open-DashboardForInsets {
    Invoke-AdbCommand shell am force-stop $pkg | Out-Null
    Grant-RequiredPermissions
    Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
    Start-Sleep -Seconds 4
    Dismiss-OnboardingIfPresent
    return Get-UiDumpContent
}

function Assert-BottomChromeAboveNavBar {
    param(
        [string]$Content,
        [string]$Label,
        [ValidateSet("three-button", "gesture")][string]$NavMode
    )
    $bounds = Get-TapTargetBounds -Content $Content -Label $Label
    if (-not $bounds) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "$Label not found in UI dump" } 1
    }
    $screenH = Get-ScreenHeightPx
    $reserve = Get-NavBarReservePx -Mode $NavMode
    $maxBottom = $screenH - $reserve
    if ($bounds.Bottom -gt $maxBottom) {
        Write-JsonResult @{
            status = "fail"
            scenario = $Scenario
            reason = "$Label bottom=$($bounds.Bottom) overlaps nav zone (screen=$screenH reserve=$reserve mode=$NavMode)"
        } 1
    }
    $tapY = [int](($bounds.Top + $bounds.Bottom) / 2)
    if ($tapY -gt $maxBottom) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "$Label tap center in nav zone" } 1
    }
}

function Assert-PlaybackScrubberAboveNavBar {
    param(
        [string]$Content,
        [ValidateSet("three-button", "gesture")][string]$NavMode
    )
    if ($Content -notmatch 'content-desc="playback index \d+ of \d+"') {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "playback screen not open for scrubber check" } 1
    }
    $screenH = Get-ScreenHeightPx
    $reserve = Get-NavBarReservePx -Mode $NavMode
    $maxBottom = $screenH - $reserve
    if ($Content -match 'text="Play"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
        $playBottom = [int]$Matches[4]
        if ($playBottom -gt $maxBottom) {
            Write-JsonResult @{
                status = "fail"
                scenario = $Scenario
                reason = "playback Play control bottom=$playBottom overlaps nav zone"
            } 1
        }
    }
    if ($Content -match 'text="Close"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
        $closeBottom = [int]$Matches[4]
        if ($closeBottom -gt $maxBottom) {
            Write-JsonResult @{
                status = "fail"
                scenario = $Scenario
                reason = "playback Close control bottom=$closeBottom overlaps nav zone"
            } 1
        }
    }
}

function Open-ImuManagement {
    Open-SettingsScreen | Out-Null
    $content = Scroll-SettingsUntilText -Label "IMU Devices"
    $imu = Find-TapTarget -Content $content -Label "IMU Devices"
    if (-not $imu) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "IMU Devices entry not found" } 1
    }
    Invoke-AdbCommand shell input tap $imu.X $imu.Y | Out-Null
    Start-Sleep -Seconds 2
    return Get-UiDumpContent
}

function Invoke-Logcat {
    param([string[]]$LogcatArgs)
    if ($Serial) {
        & adb -s $Serial logcat @LogcatArgs 2>$null
    } else {
        & adb logcat @LogcatArgs 2>$null
    }
}

function Get-ImuFusionLog {
    (Invoke-Logcat @("-d", "-s", "ExpeditionGauge/ImuFusion") | Out-String)
}

function Get-ObdLog {
    (Invoke-Logcat @("-d", "-s", "ExpeditionGauge/Obd") | Out-String)
}

function Get-TpmsLog {
    (Invoke-Logcat @("-d", "-s", "ExpeditionGauge/Tpms") | Out-String)
}

function Get-GpsLog {
    (Invoke-Logcat @("-d", "-s", "ExpeditionGauge/Gps") | Out-String)
}

function Scroll-SettingsUntilText {
    param([string]$Label)
    $content = Get-UiDumpContent
    $escaped = [regex]::Escape($Label)
    foreach ($unused in 1..8) {
        if ($content -match "text=`"$escaped`"") { return $content }
        Invoke-AdbCommand shell input swipe 1580 1200 1580 400 300 | Out-Null
        Start-Sleep -Milliseconds 400
        $content = Get-UiDumpContent
    }
    return $content
}

function Get-LatGAlertCount {
    $logs = Invoke-Logcat @("-d", "-s", "ExpeditionGauge/Alerts") | Out-String
    return ([regex]::Matches($logs, "fired type=LAT_G")).Count
}

function Clear-AlertLogcat {
    Invoke-Logcat @("-c") | Out-Null
}

function Enable-LowLatGAlerts {
    Open-SettingsScreen | Out-Null
    $content = Scroll-SettingsUntilText -Label "Enable alerts"
    if ($content -notmatch "Enable alerts") {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "alerts master toggle not found" } 1
    }
    $toggle = Find-TapTarget -Content $content -Label "Enable alerts"
    if ($toggle) {
        Invoke-AdbCommand shell input tap ($toggle.X + 220) $toggle.Y | Out-Null
    }
    Start-Sleep -Seconds 1
    $content = Scroll-SettingsUntilText -Label "Max lateral G"
    if ($content -notmatch "Max lateral G") {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Max lateral G field not found" } 1
    }
    $field = Find-TapTarget -Content $content -Label "Max lateral G"
    if ($field) {
        Invoke-AdbCommand shell input tap $field.X $field.Y | Out-Null
        Start-Sleep -Milliseconds 500
        Invoke-AdbCommand shell input keyevent KEYCODE_MOVE_END | Out-Null
        for ($i = 0; $i -lt 6; $i++) { Invoke-AdbCommand shell input keyevent 67 | Out-Null }
        Invoke-AdbCommand shell input text "0" | Out-Null
        Invoke-AdbCommand shell input keyevent KEYCODE_PERIOD | Out-Null
        Invoke-AdbCommand shell input text "94" | Out-Null
        Invoke-AdbCommand shell input keyevent KEYCODE_ENTER | Out-Null
    }
    Start-Sleep -Seconds 3
    Invoke-AdbCommand shell input keyevent KEYCODE_BACK | Out-Null
    Start-Sleep -Milliseconds 500
    $content = Scroll-SettingsUntilText -Label "Close"
    $close = Find-TapTarget -Content $content -Label "Close"
    if ($close) { Invoke-AdbCommand shell input tap $close.X $close.Y | Out-Null }
    Start-Sleep -Seconds 3
    Dismiss-OnboardingIfPresent
    $content = Get-UiDumpContent
    if ($content -notmatch "Pitch:") {
        Invoke-AdbCommand shell am force-stop $pkg | Out-Null
        Grant-RequiredPermissions
        Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
        Start-Sleep -Seconds 4
        Dismiss-OnboardingIfPresent
    }
}

function Open-TpmsManagement {
    Open-SettingsScreen | Out-Null
    $content = Scroll-SettingsUntilText -Label "TPMS Sensors"
    $manage = Find-TapTarget -Content $content -Label "TPMS Sensors"
    if (-not $manage) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "TPMS Sensors entry not found" } 1
    }
    Invoke-AdbCommand shell input tap $manage.X $manage.Y | Out-Null
    Start-Sleep -Seconds 2
    return Get-UiDumpContent
}

function Grant-RequiredPermissions {
    $perms = @(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.BLUETOOTH_SCAN"
    )
    foreach ($perm in $perms) {
        Invoke-AdbCommand shell pm grant $pkg $perm 2>$null | Out-Null
    }
}

function Dismiss-OnboardingIfPresent {
    $content = Get-UiDumpContent
    $skip = Find-TapTarget -Content $content -Label "Skip tour"
    if ($skip) {
        Invoke-AdbCommand shell input tap $skip.X $skip.Y | Out-Null
        Start-Sleep -Seconds 2
        Dismiss-OnboardingIfPresent
        return
    }
    foreach ($label in @("Not now", "Get started", "Next")) {
        $btn = Find-TapTarget -Content $content -Label $label
        if ($btn) {
            Invoke-AdbCommand shell input tap $btn.X $btn.Y | Out-Null
            Start-Sleep -Seconds 1
            Dismiss-OnboardingIfPresent
            return
        }
    }
}

function Open-SettingsScreen {
    Invoke-AdbCommand shell am force-stop $pkg | Out-Null
    Grant-RequiredPermissions
    Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
    Start-Sleep -Seconds 3
    Dismiss-OnboardingIfPresent
    $content = Get-UiDumpContent
    $settings = Find-TapTarget -Content $content -Label "Settings"
    if (-not $settings) {
        $matched = $content -match 'content-desc="Settings"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"'
        if (-not $matched) {
            $matched = $content -match 'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"[^>]*content-desc="Settings"'
        }
        if ($matched) {
            $settings = @{
                X = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
                Y = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
            }
        }
    }
    if (-not $settings) {
        Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Settings button not found" } 1
    }
    Invoke-AdbCommand shell input tap $settings.X $settings.Y | Out-Null
    Start-Sleep -Seconds 2
    return Get-UiDumpContent
}

function Count-WitMotionDevices {
    param([string]$Content)
    $names = @()
    if ($Content -match "WT901") { $names += "WT901" }
    if ($Content -match "WitMotion") { $names += "WitMotion" }
    if ($Content -match "WT61") { $names += "WT61" }
    return $names.Count
}

function Test-RecordingUiActive {
    param([string]$Content)
    if ($Content -match 'recording_live_strip') { return $true }
    if ($Content -match 'text="Stop"') { return $true }
    if ($Content -match 'record_stop') { return $true }
    if ($Content -match 'text="Record"' -and $Content -match 'crawl_badge') { return $true }
    return $false
}

function Test-RecordingSessionEnded {
    param([string]$Content)
    return ($Content -match 'text="Record"' -and $Content -notmatch 'recording_live_strip')
}
