function Invoke-AdbReliveScenario {
    param([string]$Name)
    switch ($Name) {
    "media-attach-recording" {
        Invoke-AdbCommand shell am force-stop $pkg | Out-Null
        Grant-RequiredPermissions
        $mediaBaseline = @(
            (Invoke-AdbCommand shell run-as $pkg find files/sessions -type f 2>$null) -split "`n" |
                Where-Object { $_ -match "photo_" }
        ).Count
        Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
        Start-Sleep -Seconds 4
        Dismiss-OnboardingIfPresent
        $content = Get-UiDumpContent
        $record = Find-TapTarget -Content $content -Label "Record"
        if (-not $record) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Record not found" } 1
        }
        Invoke-AdbCommand shell input tap $record.X $record.Y | Out-Null
        Start-Sleep -Seconds 3
        $content = Get-UiDumpContent
        $advanced = Find-TapTarget -Content $content -Label "Recording options"
        if ($advanced) {
            Invoke-AdbCommand shell input tap $advanced.X $advanced.Y | Out-Null
            Start-Sleep -Seconds 2
        }
        $content = Get-UiDumpContent
        $stub = Find-TapTarget -Content $content -Label "Attach test photo"
        if (-not $stub) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "attach_media_stub not found" } 1
        }
        Invoke-AdbCommand shell input tap $stub.X $stub.Y | Out-Null
        Start-Sleep -Seconds 1
        Invoke-AdbCommand shell input keyevent 4 | Out-Null
        Start-Sleep -Seconds 1
        $mediaBefore = (Invoke-AdbCommand shell run-as $pkg find files/sessions -type f 2>$null) -join "`n"
        if ($mediaBefore -notmatch "photo_") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "media file not written" } 1
        }
        $content = Get-UiDumpContent
        $stop = Find-TapTarget -Content $content -Label "Stop"
        if (-not $stop) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Stop not found after attach" } 1
        }
        Invoke-AdbCommand shell input tap $stop.X $stop.Y | Out-Null
        Start-Sleep -Seconds 2
        $content = Get-UiDumpContent
        $sessions = Find-TapTarget -Content $content -Label "Sessions"
        if (-not $sessions) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Sessions button not found" } 1
        }
        Invoke-AdbCommand shell input tap $sessions.X $sessions.Y | Out-Null
        Start-Sleep -Seconds 3
        $content = Get-UiDumpContent
        if (-not (Find-TapTarget -Content $content -Label "Play")) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "no session in list" } 1
        }
        Invoke-AdbCommand shell input swipe 500 1800 500 400 300 | Out-Null
        Start-Sleep -Seconds 1
        $content = Get-UiDumpContent
        $edit = Find-TapTarget -Content $content -Label "Edit metadata"
        if (-not $edit) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Edit metadata not found" } 1
        }
        Invoke-AdbCommand shell input tap $edit.X $edit.Y | Out-Null
        Start-Sleep -Seconds 2
        foreach ($unused in 1..3) {
            Invoke-AdbCommand shell input swipe 500 1800 500 400 300 | Out-Null
            Start-Sleep -Milliseconds 400
        }
        $content = Get-UiDumpContent
        $delete = Find-TapTarget -Content $content -Label "Delete session"
        if (-not $delete) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Delete session not found" } 1
        }
        Invoke-AdbCommand shell input tap $delete.X $delete.Y | Out-Null
        Start-Sleep -Seconds 2
        $mediaAfter = @(
            (Invoke-AdbCommand shell run-as $pkg find files/sessions -type f 2>$null) -split "`n" |
                Where-Object { $_ -match "photo_" }
        ).Count
        if ($mediaAfter -ge ($mediaBaseline + 1)) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "media files remain after delete" } 1
        }
        Assert-ActivityVisible
    }
    "library-filter-tag" {
        Invoke-AdbCommand shell am force-stop $pkg | Out-Null
        Grant-RequiredPermissions
        Invoke-AdbCommand shell am start -n "$pkg/.MainActivity" | Out-Null
        Start-Sleep -Seconds 3
        Dismiss-OnboardingIfPresent
        $content = Get-UiDumpContent
        $record = Find-TapTarget -Content $content -Label "Record"
        if ($record) {
            Invoke-AdbCommand shell input tap $record.X $record.Y | Out-Null
            Start-Sleep -Seconds 5
            $content = Get-UiDumpContent
            $stop = Find-TapTarget -Content $content -Label "Stop"
            if ($stop) { Invoke-AdbCommand shell input tap $stop.X $stop.Y | Out-Null }
            Start-Sleep -Seconds 2
        }
        $content = Get-UiDumpContent
        foreach ($unused in 1..3) {
            $sessionsBtn = Find-TapTarget -Content $content -Label "Sessions"
            if ($sessionsBtn) { break }
            Invoke-AdbCommand shell input swipe 1580 1800 1580 600 300 | Out-Null
            Start-Sleep -Milliseconds 400
            $content = Get-UiDumpContent
        }
        $sessionsBtn = Find-TapTarget -Content $content -Label "Sessions"
        if (-not $sessionsBtn) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Sessions button not found" } 1
        }
        Invoke-AdbCommand shell input tap $sessionsBtn.X $sessionsBtn.Y | Out-Null
        Start-Sleep -Seconds 2
        $content = Get-UiDumpContent
        if ($content -notmatch "route preview") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "session route thumbnail not visible" } 1
        }
        $edit = Find-TapTarget -Content $content -Label "Edit metadata"
        if (-not $edit) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Edit metadata button not found" } 1
        }
        Invoke-AdbCommand shell input tap $edit.X $edit.Y | Out-Null
        Start-Sleep -Seconds 2
        foreach ($unused in 1..2) {
            Invoke-AdbCommand shell input swipe 500 1800 500 400 300 | Out-Null
            Start-Sleep -Milliseconds 400
        }
        $offroadChip = Find-TapTarget -Content (Get-UiDumpContent) -Label "Off-road"
        if (-not $offroadChip) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Off-road activity chip not found" } 1
        }
        Invoke-AdbCommand shell input tap $offroadChip.X $offroadChip.Y | Out-Null
        Start-Sleep -Seconds 1
        foreach ($unused in 1..5) {
            Invoke-AdbCommand shell input swipe 500 1800 500 400 300 | Out-Null
            Start-Sleep -Milliseconds 400
        }
        $content = Get-UiDumpContent
        $save = Find-TapTarget -Content $content -Label "Save metadata"
        if (-not $save -and $content -match 'text="Save metadata"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"') {
            $save = @{
                X = [int](([int]$Matches[1] + [int]$Matches[3]) / 2)
                Y = [int](([int]$Matches[2] + [int]$Matches[4]) / 2)
            }
        }
        if (-not $save) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Save metadata button not found" } 1
        }
        Invoke-AdbCommand shell input tap $save.X $save.Y | Out-Null
        Start-Sleep -Seconds 2
        $content = Get-UiDumpContent
        $filterOffroad = Find-TapTarget -Content $content -Label "Off-road"
        if (-not $filterOffroad) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Off-road filter chip not found" } 1
        }
        Invoke-AdbCommand shell input tap $filterOffroad.X $filterOffroad.Y | Out-Null
        Start-Sleep -Seconds 2
        $content = Get-UiDumpContent
        if (-not (Find-TapTarget -Content $content -Label "Play")) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "session not visible after OFFROAD filter" } 1
        }
        if ($content -notmatch "route preview") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "route thumbnail missing after filter" } 1
        }
        if ($content -notmatch "Activity: Off-road") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "activity type Off-road not shown on card" } 1
        }
        Assert-ActivityVisible
    }
    "playback-video-export" {
        $content = Open-PlaybackScreen
        foreach ($unused in 1..4) {
            Invoke-AdbCommand shell input swipe 500 1800 500 400 300 | Out-Null
            Start-Sleep -Milliseconds 400
        }
        $content = Get-UiDumpContent
        if ($content -notmatch "Export playback video") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "playback export panel not visible" } 1
        }
        $exportBtn = Find-TapTarget -Content $content -Label "Export video"
        if (-not $exportBtn) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Export video button not found" } 1
        }
        Invoke-AdbCommand shell input tap $exportBtn.X $exportBtn.Y | Out-Null
        $complete = $false
        foreach ($unused in 1..90) {
            Start-Sleep -Seconds 2
            $content = Get-UiDumpContent
            if ($content -match "Export complete") {
                $complete = $true
                break
            }
            if ($content -match "Encoding") {
                continue
            }
        }
        if (-not $complete) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "export did not complete within timeout" } 1
        }
        if (-not (Find-TapTarget -Content $content -Label "Share video")) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Share video button not found after export" } 1
        }
        Assert-ActivityVisible
    }
    "flyover-video-export" {
        $content = Open-PlaybackScreen
        foreach ($unused in 1..6) {
            Invoke-AdbCommand shell input swipe 500 1800 500 400 300 | Out-Null
            Start-Sleep -Milliseconds 400
        }
        $content = Get-UiDumpContent
        if ($content -notmatch "3D route flyover") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "flyover export panel not visible" } 1
        }
        $createBtn = Find-TapTarget -Content $content -Label "Create 3D video"
        if (-not $createBtn) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Create 3D video button not found" } 1
        }
        Invoke-AdbCommand shell input tap $createBtn.X $createBtn.Y | Out-Null
        $complete = $false
        foreach ($unused in 1..120) {
            Start-Sleep -Seconds 2
            $content = Get-UiDumpContent
            if ($content -match "Flyover complete") {
                $complete = $true
                break
            }
        }
        if (-not $complete) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "flyover export did not complete within timeout" } 1
        }
        if (-not (Find-TapTarget -Content $content -Label "Share flyover")) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Share flyover button not found" } 1
        }
        Assert-ActivityVisible
    }
    "sharing-video-card" {
        $content = Open-PlaybackScreen
        foreach ($unused in 1..4) {
            Invoke-AdbCommand shell input swipe 500 1800 500 400 300 | Out-Null
            Start-Sleep -Milliseconds 400
        }
        $content = Get-UiDumpContent
        if ($content -notmatch "Export playback video") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "playback export panel not visible" } 1
        }
        $shareBtn = Find-TapTarget -Content $content -Label "Share video"
        if ($shareBtn) {
            Invoke-AdbCommand shell input tap $shareBtn.X $shareBtn.Y | Out-Null
            Start-Sleep -Seconds 1
            $content = Get-UiDumpContent
            if ($content -notmatch "Share session") {
                Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "share preview sheet not shown" } 1
            }
            foreach ($unused in 1..2) {
                Invoke-AdbCommand shell input swipe 500 1800 500 900 250 | Out-Null
                Start-Sleep -Milliseconds 300
            }
            $content = Get-UiDumpContent
            $confirmBtn = Find-TapTarget -Content $content -Label "Share video and card"
            if (-not $confirmBtn) {
                Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "share preview confirm button not found" } 1
            }
            Invoke-AdbCommand shell input tap $confirmBtn.X $confirmBtn.Y | Out-Null
            Start-Sleep -Seconds 2
            Assert-ActivityVisible
            return $true
        }
        $exportBtn = Find-TapTarget -Content $content -Label "Export video"
        if (-not $exportBtn) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Export video button not found" } 1
        }
        Invoke-AdbCommand shell input tap $exportBtn.X $exportBtn.Y | Out-Null
        $complete = $false
        foreach ($unused in 1..90) {
            Start-Sleep -Seconds 2
            $content = Get-UiDumpContent
            if ($content -match "Export complete") {
                $complete = $true
                break
            }
        }
        if (-not $complete) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "export did not complete within timeout" } 1
        }
        $shareBtn = Find-TapTarget -Content $content -Label "Share video"
        if (-not $shareBtn) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "Share video button not found after export" } 1
        }
        Invoke-AdbCommand shell input tap $shareBtn.X $shareBtn.Y | Out-Null
        Start-Sleep -Seconds 1
        $content = Get-UiDumpContent
        if ($content -notmatch "Share session") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "share preview sheet not shown" } 1
        }
        if ($content -notmatch "Stats card preview") {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "stats card preview label not found" } 1
        }
        foreach ($unused in 1..2) {
            Invoke-AdbCommand shell input swipe 500 1800 500 900 250 | Out-Null
            Start-Sleep -Milliseconds 300
        }
        $content = Get-UiDumpContent
        $confirmBtn = Find-TapTarget -Content $content -Label "Share video and card"
        if (-not $confirmBtn) {
            Write-JsonResult @{ status = "fail"; scenario = $Scenario; reason = "share preview confirm button not found" } 1
        }
        Invoke-AdbCommand shell input tap $confirmBtn.X $confirmBtn.Y | Out-Null
        Start-Sleep -Seconds 2
        Assert-ActivityVisible
    }
        default { return $false }
    }
    return $true
}
