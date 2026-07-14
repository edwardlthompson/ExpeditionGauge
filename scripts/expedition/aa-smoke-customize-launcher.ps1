# Smoke: is ExpeditionGauge visible in Android Auto Customize launcher?
# Requires rooted device (OnePlus 12). Applies Play-installer reinstall + phenotype whitelist,
# then dumps the Customize launcher UI hierarchy and greps for the app.
param(
    [string]$Serial = "",
    [string]$Apk = "",
    [switch]$SkipPhenotype,
    [switch]$SkipReinstall
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial -Config $config }
if (-not $Serial) { Write-Error "Need -Serial" }

$pkg = "dev.foss.expeditiongauge"
$aa = "com.google.android.projection.gearhead"
$label = "ExpeditionGauge"
$outDir = Join-Path $Root ".cursor"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

function Invoke-AdbCmd([string[]]$CmdArgs) {
    & adb.exe -s $Serial @CmdArgs
    if ($LASTEXITCODE -ne 0) { throw "adb failed: $($CmdArgs -join ' ')" }
}

function Ensure-Root {
    Invoke-AdbCmd @("root") | Out-Null
    Start-Sleep -Seconds 1
    $id = (& adb.exe -s $Serial shell id) -join ""
    if ($id -notmatch "uid=0") { Write-Error "aa-smoke-customize-launcher: need root (got $id)" }
}

function Dump-PackageFacts {
    $dump = (Invoke-AdbCmd @("shell", "dumpsys", "package", $pkg)) -join "`n"
    $facts = [ordered]@{
        versionName = if ($dump -match "versionName=(\S+)") { $Matches[1] } else { "?" }
        categoryPOI = [bool]($dump -match "androidx\.car\.app\.category\.POI")
        installer = if ($dump -match "installerPackageName=(\S+)") { $Matches[1] } else { "null" }
        initiator = if ($dump -match "initiatingPackageName=(\S+)") { $Matches[1] } else { "null" }
        unknownSources = $false
    }
    $prefs = (& adb.exe -s $Serial shell "cat /data/data/$aa/shared_prefs/action_developer_settings.xml 2>/dev/null") -join "`n"
    if (-not $prefs) {
        $prefs = (& adb.exe -s $Serial shell "cat /data/user/0/$aa/shared_prefs/action_developer_settings.xml 2>/dev/null") -join "`n"
    }
    $facts.unknownSources = $prefs -match 'allow_unknown_sources" value="true"'
    return $facts
}

function Open-CustomizeLauncher {
    # Companion settings home, then try deep-link / preference search via UI.
    Invoke-AdbCmd @("shell", "am", "force-stop", $aa) | Out-Null
    Start-Sleep -Milliseconds 400
    Invoke-AdbCmd @("shell", "am", "start", "-a", "com.google.android.projection.gearhead.SETTINGS", "-n", "$aa/.companion.settings.DefaultSettingsActivity") | Out-Null
    Start-Sleep -Seconds 2
}

function Dump-Ui([string]$Tag) {
    $remote = "/sdcard/aa-ui-$Tag.xml"
    $local = Join-Path $outDir "aa-ui-$Tag.xml"
    Invoke-AdbCmd @("shell", "uiautomator", "dump", $remote) | Out-Null
    Invoke-AdbCmd @("pull", $remote, $local) | Out-Null
    return Get-Content $local -Raw -ErrorAction SilentlyContinue
}

function Tap-Text([string]$Ui, [string]$Text) {
    # Parse bounds for a node whose text/content-desc contains Text.
    $escaped = [regex]::Escape($Text)
    $m = [regex]::Match($Ui, "text=`"$escaped`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]")
    if (-not $m.Success) {
        $m = [regex]::Match($Ui, "content-desc=`"$escaped`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]")
    }
    if (-not $m.Success) {
        # Case-insensitive partial
        $m = [regex]::Match($Ui, "(?i)(?:text|content-desc)=`"[^`"]*$escaped[^`"]*`"[^>]*bounds=`"\[(\d+),(\d+)\]\[(\d+),(\d+)\]")
    }
    if (-not $m.Success) { return $false }
    $x = [int](([int]$m.Groups[1].Value + [int]$m.Groups[3].Value) / 2)
    $y = [int](([int]$m.Groups[2].Value + [int]$m.Groups[4].Value) / 2)
    Invoke-AdbCmd @("shell", "input", "tap", "$x", "$y") | Out-Null
    Start-Sleep -Seconds 1
    return $true
}

function Test-ListedInCustomize {
    Open-CustomizeLauncher
    $ui = Dump-Ui "settings-home"
    $tapped = $false
    foreach ($candidate in @("Customize launcher", "Customise launcher", "Customize Launcher", "Launcher", "App drawer")) {
        if (Tap-Text $ui $candidate) { $tapped = $true; break }
    }
    if (-not $tapped) {
        # Scroll and retry once
        Invoke-AdbCmd @("shell", "input", "swipe", "500", "1600", "500", "400", "300") | Out-Null
        Start-Sleep -Seconds 1
        $ui = Dump-Ui "settings-scrolled"
        foreach ($candidate in @("Customize launcher", "Customise launcher", "Customize Launcher", "Apps")) {
            if (Tap-Text $ui $candidate) { $tapped = $true; break }
        }
    }
    $ui2 = Dump-Ui "customize"
    $listed = ($ui2 -match [regex]::Escape($label)) -or ($ui2 -match [regex]::Escape($pkg))
    return @{
        openedCustomize = $tapped
        listed = $listed
        uiSnippet = if ($ui2) { ($ui2 -replace '>\s*<', ">`n<").Split("`n") | Where-Object { $_ -match 'text=|content-desc=' } | Select-Object -First 80 } else { @() }
    }
}

function Apply-Phenotype {
    $sqlLocal = Join-Path $PSScriptRoot "aa-phenotype-whitelist.sql"
    $sqlRemote = "/data/local/tmp/aa-phenotype-whitelist.sql"
    Invoke-AdbCmd @("push", $sqlLocal, $sqlRemote) | Out-Null
    $owner = (Invoke-AdbCmd @("shell", "stat", "-c", "%U", "/data/data/com.google.android.gms/databases/phenotype.db")) -join ""
    Invoke-AdbCmd @("shell", "am", "force-stop", "com.google.android.gms") | Out-Null
    Invoke-AdbCmd @("shell", "am", "force-stop", $aa) | Out-Null
    Invoke-AdbCmd @("shell", "chown", "root:root", "/data/data/com.google.android.gms/databases/phenotype.db") | Out-Null
    $apply = Invoke-AdbCmd @("shell", "/system/bin/sqlite3", "/data/data/com.google.android.gms/databases/phenotype.db", ".read $sqlRemote")
    Invoke-AdbCmd @("shell", "chown", "${owner}:${owner}", "/data/data/com.google.android.gms/databases/phenotype.db") | Out-Null
    $check = (Invoke-AdbCmd @("shell", "/system/bin/sqlite3", "/data/data/com.google.android.gms/databases/phenotype.db", "SELECT name, stringVal, boolVal FROM FlagOverrides WHERE name LIKE '%AppValidation%' OR name LIKE '%white_list%';")) -join "`n"
    return @{ apply = ($apply -join "`n"); flags = $check }
}

# --- run ---
Ensure-Root
Write-Host "=== ATTEMPT 0: baseline facts ===" -ForegroundColor Cyan
$facts0 = Dump-PackageFacts
$facts0 | ConvertTo-Json -Compress | Write-Host
$smoke0 = Test-ListedInCustomize
Write-Host ("listed={0} openedCustomize={1}" -f $smoke0.listed, $smoke0.openedCustomize)
if ($smoke0.listed) {
    Write-JsonResult @{ status = "ok"; attempt = "baseline_after_play_spoof"; listed = $true; facts = $facts0 } 0
}

if (-not $SkipReinstall) {
    Write-Host "=== ATTEMPT 1: reinstall -t -i com.android.vending ===" -ForegroundColor Cyan
    if (-not $Apk) {
        $Apk = Get-ChildItem (Join-Path $Root "ExpeditionGauge-*.apk") | Sort-Object LastWriteTime -Descending | Select-Object -First 1 -ExpandProperty FullName
    }
    if (-not $Apk -or -not (Test-Path $Apk)) { Write-Error "APK required (-Apk)" }
    $remoteApk = "/data/local/tmp/eg-aa.apk"
    Invoke-AdbCmd @("push", $Apk, $remoteApk) | Out-Null
    # AA-Tweaker uses -t (test) + -i vending after mv/uninstall cycle for stubborn hosts
    Invoke-AdbCmd @("shell", "pm", "path", $pkg) | Out-Null
    Invoke-AdbCmd @("shell", "pm", "uninstall", $pkg) | Out-Null
    Invoke-AdbCmd @("shell", "pm", "install", "-t", "-i", "com.android.vending", "-r", $remoteApk)
    Invoke-AdbCmd @("shell", "cmd", "package", "set-installer", $pkg, "com.android.vending") | Out-Null
    Invoke-AdbCmd @("shell", "am", "start", "-n", "$pkg/.MainActivity") | Out-Null
    Start-Sleep -Seconds 2
    $facts1 = Dump-PackageFacts
    $facts1 | ConvertTo-Json -Compress | Write-Host
    $smoke1 = Test-ListedInCustomize
    Write-Host ("AFTER REINSTALL listed={0} openedCustomize={1}" -f $smoke1.listed, $smoke1.openedCustomize)
    if ($smoke1.listed) {
        Write-JsonResult @{ status = "ok"; attempt = "reinstall_vending_test"; listed = $true; facts = $facts1 } 0
    }
}

if (-not $SkipPhenotype) {
    Write-Host "=== ATTEMPT 2: phenotype AppValidation whitelist ===" -ForegroundColor Cyan
    $ph = Apply-Phenotype
    Write-Host $ph.flags
    Invoke-AdbCmd @("shell", "am", "force-stop", $aa) | Out-Null
    Invoke-AdbCmd @("shell", "am", "force-stop", "com.google.android.gms") | Out-Null
    Start-Sleep -Seconds 2
    $smoke2 = Test-ListedInCustomize
    Write-Host ("AFTER PHENOTYPE listed={0} openedCustomize={1}" -f $smoke2.listed, $smoke2.openedCustomize)
    $smoke2.uiSnippet | ForEach-Object { Write-Host $_ }
    if ($smoke2.listed) {
        Write-JsonResult @{ status = "ok"; attempt = "phenotype_whitelist"; listed = $true } 0
    }
    else {
        Write-JsonResult @{
            status = "fail"
            attempt = "phenotype_whitelist"
            listed = $false
            openedCustomize = $smoke2.openedCustomize
            facts = (Dump-PackageFacts)
            hint = "UI dump saved under .cursor/aa-ui-*.xml — if openedCustomize=false, navigate manually once so we learn the label"
        } 1
    }
}

Write-JsonResult @{ status = "fail"; listed = $false; reason = "exhausted attempts" } 1
