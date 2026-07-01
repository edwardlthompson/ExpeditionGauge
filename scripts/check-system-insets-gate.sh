#!/usr/bin/env bash
# System UI insets gate (Sprint 19b+).
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

echo "=== system UI insets gate (Sprint 19b+) ==="

check_file "$DOCS/system-ui-insets.md"
check_file "$ANDROID/ui/layout/InsetAwareScaffold.kt"
check_file "$ANDROID/ui/layout/NavigationBarBottomPadding.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/ui/layout/NavigationBarBottomPaddingTest.kt"

grep_file "$ANDROID/ui/components/gauge/RecordControls.kt" "navigationBarBottomPadding"
grep_file "$ANDROID/ui/playback/ScrubberMarkerStrip.kt" "navigationBarBottomPadding"
grep_file "$ANDROID/ui/playback/PlaybackMapView.kt" "rememberPlaybackMapOrnamentOptions"
grep_file "$ANDROID/ui/playback/PlaybackMapOrnamentOptions.kt" "WindowInsets\\.navigationBars"
grep_file "$ANDROID/ui/components/gauge/AttitudeGMeterGauge.kt" "contentWindowInsets"
grep_file "$ANDROID/ui/recording/RecordingLiveStrip.kt" "contentWindowInsets"
grep_file "$ANDROID/ui/navigation/ExpeditionGaugeApp.kt" "WindowInsets\\.statusBars"

if [ "$FAIL" -ne 0 ]; then
  echo "system UI insets gate FAILED"
  exit 1
fi
echo "system UI insets gate passed"
