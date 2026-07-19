#!/usr/bin/env bash
# Android Auto gate (Sprint 21+).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ANDROID="$ROOT/examples/android/app/src/main/java/dev/foss/expeditiongauge"
CAR="$ROOT/examples/android/car/src/main/java/dev/foss/expeditiongauge/car"
DOCS="$ROOT/docs/features"
FAIL=0

check_file() {
  if [ -f "$1" ]; then
    echo "OK   $1"
  else
    echo "MISSING $1"
    FAIL=1
  fi
}

grep_file() {
  local path="$1"
  local pattern="$2"
  if grep -qE "$pattern" "$path" 2>/dev/null; then
    echo "OK   $path ($pattern)"
  else
    echo "MISSING pattern in $path: $pattern"
    FAIL=1
  fi
}

echo "=== v2 car gate (Sprint 21+) ==="

check_file "$DOCS/android-auto.md"
check_file "$ROOT/docs/adr/0010-android-auto.md"
check_file "$ROOT/docs/design/CAR_GAUGE_PRIORITY.md"
check_file "$CAR/ExpeditionGaugeCarAppService.kt"
check_file "$CAR/CarAppBridge.kt"
check_file "$CAR/DriveHudContent.kt"
check_file "$CAR/ui/DriveMapHudScreen.kt"
check_file "$CAR/ui/DriveMapHudChrome.kt"
check_file "$CAR/ui/DrivePaneScreen.kt"
check_file "$CAR/ui/DrivePaneTemplates.kt"
check_file "$CAR/ui/TelemetryGridActions.kt"
check_file "$CAR/gauge/InclinometerBitmapRenderer.kt"
check_file "$CAR/gauge/InclinometerSegmentLogic.kt"
check_file "$CAR/gauge/DriveHudBitmapRenderer.kt"
check_file "$CAR/gauge/DriveHudCarIcon.kt"
check_file "$CAR/surface/DriveHudSurfacePainter.kt"
check_file "$ROOT/scripts/expedition/dhu-preview.ps1"
check_file "$ROOT/scripts/expedition/dhu-smoke.ps1"
check_file "$ROOT/scripts/expedition/aa-bitmap-preview.ps1"
check_file "$ROOT/docs/design/AA_INCLINOMETER.md"
check_file "$ANDROID/car/AndroidAutoBridge.kt"
check_file "$ANDROID/ui/settings/SettingsAndroidAutoOptions.kt"
check_file "$ROOT/examples/android/app/src/main/res/xml/automotive_app_desc.xml"
check_file "$ROOT/examples/android/car/src/test/java/dev/foss/expeditiongauge/car/ui/DrivePaneTemplatesTest.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/car/AndroidAutoBridgeMetricsTest.kt"

grep_file "$ROOT/examples/android/app/build.gradle.kts" 'project\(":car"\)'
grep_file "$ROOT/examples/android/app/src/main/AndroidManifest.xml" "androidx.car.app.CarAppService"
grep_file "$ROOT/examples/android/app/src/main/AndroidManifest.xml" "androidx.car.app.category.POI"
grep_file "$ROOT/examples/android/app/src/main/AndroidManifest.xml" 'androidx.car.app.minCarApiLevel'
grep_file "$ROOT/examples/android/app/src/main/AndroidManifest.xml" 'android:value="7"'
grep_file "$ROOT/examples/android/app/src/main/AndroidManifest.xml" "automotive_app_desc"
grep_file "$ANDROID/ExpeditionGaugeApplication.kt" "CarAppBridgeRegistry"
grep_file "$ANDROID/FeatureFlags.kt" "androidAutoEnabled"
grep_file "$CAR/ExpeditionGaugeCarAppService.kt" "DriveMapHudScreen"
grep_file "$CAR/ui/DriveMapHudScreen.kt" "NavigationTemplate"
grep_file "$CAR/ui/DrivePaneScreen.kt" "PaneTemplate"
grep_file "$CAR/ui/DrivePaneScreen.kt" "setImage"
grep_file "$CAR/CarAppBridge.kt" "cycleAttitudeDisplay"
grep_file "$CAR/CarAppBridge.kt" "driveHud"
grep_file "$CAR/CarAppBridge.kt" "zeroAttitude"
grep_file "$ANDROID/car/AaScreenInvalidation.kt" "AA_INVALIDATE_MIN_INTERVAL_MS"
if grep -qE '"Mark"' "$CAR/ui/DrivePaneScreen.kt" 2>/dev/null; then
  echo "FAIL Mark action still present in DrivePaneScreen.kt"
  FAIL=1
else
  echo "OK   DrivePaneScreen.kt (no Mark action)"
fi

if [ "$FAIL" -ne 0 ]; then
  echo "v2 car gate FAILED"
  exit 1
fi
echo "v2 car gate passed"
