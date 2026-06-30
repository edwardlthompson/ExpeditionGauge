#!/usr/bin/env bash
# v2 video gate (Sprint 18+).
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

echo "=== v2 video gate (Sprint 18+) ==="

check_file "$DOCS/video-sync.md"
check_file "$DOCS/developer-mode.md"
check_file "$DOCS/enhanced-export.md"
check_file "$ANDROID/video/VideoSyncEngine.kt"
check_file "$ANDROID/video/VideoOverlayCompositor.kt"
check_file "$ANDROID/video/VideoBurnInExporter.kt"
check_file "$ANDROID/export/EnhancedExportService.kt"
check_file "$ANDROID/ui/calibration/CalibrationWizardScreen.kt"
check_file "$ANDROID/ui/developer/DeveloperModeScreen.kt"
check_file "$ANDROID/ui/playback/PlaybackVideoControls.kt"

if [ "$FAIL" -ne 0 ]; then
  echo "v2 video gate FAILED"
  exit 1
fi
echo "v2 video gate passed"
