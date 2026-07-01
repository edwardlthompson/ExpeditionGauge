#!/usr/bin/env bash
# Dual-orientation gate (Sprint 20+).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ANDROID="$ROOT/examples/android/app/src/main/java/dev/foss/expeditiongauge"
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

echo "=== v2 orientation gate (Sprint 20+) ==="

check_file "$DOCS/dual-orientation.md"
check_file "$ROOT/docs/adr/0009-dual-orientation.md"
check_file "$ANDROID/ui/orientation/OrientationLayoutEngine.kt"
check_file "$ANDROID/ui/dashboard/DashboardHudLandscape.kt"
check_file "$ANDROID/ui/dashboard/DashboardHudPortrait.kt"
check_file "$ANDROID/settings/DrivingModePreferences.kt"
check_file "$ANDROID/ExpeditionGaugeApplication.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/ui/orientation/OrientationLayoutEngineTest.kt"

grep_file "$ROOT/examples/android/app/src/main/AndroidManifest.xml" "fullUser"
grep_file "$ANDROID/ui/components/gauge/AttitudeGMeterGauge.kt" "gaugeSizeDp"
grep_file "$ANDROID/ui/components/gauge/SpeedometerGauge.kt" "gaugeSizeDp"
grep_file "$ANDROID/ui/dashboard/DashboardHudLayout.kt" "OrientationLayoutEngine"

if [ "$FAIL" -ne 0 ]; then
  echo "v2 orientation gate FAILED"
  exit 1
fi
echo "v2 orientation gate passed"
