#!/usr/bin/env bash
# Polish wave 3 gate (Sprint 17+).
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

echo "=== Polish wave 3 gate (Sprint 17+) ==="

check_file "$DOCS/session-stats.md"
check_file "$DOCS/onboarding.md"
check_file "$DOCS/accessibility.md"
check_file "$DOCS/session-comparison.md"
check_file "$ANDROID/stats/SessionStatsAggregator.kt"
check_file "$ANDROID/export/HtmlSummaryExporter.kt"
check_file "$ANDROID/recording/SessionEventRecorder.kt"
check_file "$ANDROID/onboarding/OnboardingTour.kt"
check_file "$ANDROID/ui/stats/RichSessionCard.kt"
check_file "$ANDROID/ui/calibration/CalibrationTipsScreen.kt"
check_file "$ANDROID/accessibility/MetricTtsReadout.kt"

if [ "$FAIL" -ne 0 ]; then
  echo "Polish wave 3 gate FAILED"
  exit 1
fi
echo "Polish wave 3 gate passed"
