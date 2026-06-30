#!/usr/bin/env bash
# Verify v1.1 polish wave 1 (Sprints 9–14) source artifacts exist.
# Usage: scripts/check-polish-wave1-gate.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ANDROID="$ROOT/examples/android/app/src/main/java/dev/foss/expeditiongauge"
DOCS="$ROOT/docs/features"
FAIL=0

check_file() {
  if [ ! -f "$1" ]; then
    echo "FAIL: missing $1"
    FAIL=1
  else
    echo "OK   $1"
  fi
}

echo "=== Polish wave 1 gate (Sprints 9–14) ==="

# Sprint 9
check_file "$DOCS/session-metadata.md"
check_file "$DOCS/crawling-mode.md"
check_file "$ANDROID/recording/SessionMetadata.kt"
check_file "$ANDROID/recording/CrawlingModeProfile.kt"

# Sprint 10
check_file "$DOCS/lap-timing.md"
check_file "$ROOT/docs/adr/0002-lap-timing.md"
check_file "$ANDROID/timing/LapDetector.kt"
check_file "$ANDROID/timing/SectorSplitCalculator.kt"
check_file "$ANDROID/timing/PredictiveTimingEngine.kt"

# Sprint 11
check_file "$DOCS/telemetry-graphs.md"
check_file "$DOCS/heatmap-overlay.md"
check_file "$ANDROID/ui/playback/TelemetryGraphPanel.kt"
check_file "$ANDROID/ui/playback/RouteHeatmapLayer.kt"

# Sprint 12
check_file "$DOCS/driving-line.md"
check_file "$DOCS/ghost-lap.md"
check_file "$ANDROID/drivingline/DrivingLineAnalyzer.kt"
check_file "$ANDROID/ghost/GhostLapOverlay.kt"

# Sprint 13
check_file "$DOCS/alerts.md"
check_file "$ANDROID/alerts/AlertEngine.kt"
check_file "$ANDROID/alerts/AlertThresholds.kt"
check_file "$ANDROID/alerts/AlertService.kt"
check_file "$ANDROID/alerts/AlertThresholdsPreferences.kt"

# Shared coordinator
check_file "$ANDROID/playback/PlaybackEngine.kt"
check_file "$ANDROID/FeatureFlags.kt"

if [ "$FAIL" -ne 0 ]; then
  echo "Polish wave 1 gate FAILED"
  exit 1
fi
echo "Polish wave 1 gate passed"
