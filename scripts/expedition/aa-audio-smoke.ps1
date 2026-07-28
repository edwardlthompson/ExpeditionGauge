# Smoke: trigger phone alert TTS/beep while DHU is projecting — listen for nav/media ducking.
# Prerequisites: phone projecting to DHU, ExpeditionGauge open, alerts unmuted, TTS or Beep selected.
param(
    [string]$Serial = "",
    [ValidateSet("tts", "beep")]
    [string]$Mode = "tts"
)

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot
$config = Read-ProjectConfig
if (-not $Serial) { $Serial = Get-AdbSerial -Config $config }
if (-not $Serial) { Write-Error "aa-audio-smoke: no ADB serial" }

$pkg = "dev.foss.expeditiongauge"
Write-Host "aa-audio-smoke: mode=$Mode serial=$Serial" -ForegroundColor Cyan
Write-Host "Ensure DHU is projecting ExpeditionGauge and car volume is up." -ForegroundColor Yellow

# Bring app forward; user validates audio on DHU/speakers.
& adb -s $Serial shell am start -n "$pkg/.MainActivity" | Out-Null
Start-Sleep -Seconds 1

# Broadcast is not wired; instruct manual threshold trip + log reminder.
Write-Host @"
Manual steps:
  1. Settings → Alerts → audio mode = $(if ($Mode -eq 'tts') { 'Speech (TTS)' } else { 'Beep' }), Mute OFF
  2. Temporarily set Max speed very low (e.g. 1 MPH) OR pitch/roll limit low
  3. On phone HUD, exceed the limit (or tilt for pitch/roll)
  4. Confirm audio plays on the car/DHU path (media ducks), not only phone earpiece
  5. Tap AA Mute — audio should stop; visuals remain

Log tags: ExpeditionGauge/AlertTts , ExpeditionGauge/Alerts
  adb -s $Serial logcat -s ExpeditionGauge/AlertTts:D ExpeditionGauge/Alerts:D
Expect: requestAudioFocus result=1 and speak result=0 (or beep path).
If you still see AudioHardening muting com.google.android.tts, focus was denied.
"@

Write-Host "OK  aa-audio-smoke checklist printed" -ForegroundColor Green
