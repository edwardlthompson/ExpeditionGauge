#!/usr/bin/env bash
# Activity library gate (Sprint 24+).
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

echo "=== v2 activity library gate (Sprint 24+) ==="

check_file "$DOCS/activity-library.md"
check_file "$ANDROID/stats/SessionThumbnailGenerator.kt"
check_file "$ANDROID/ui/dashboard/HomeQuickStatsStrip.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/stats/SessionThumbnailGeneratorTest.kt"

grep_file "$ANDROID/FeatureFlags.kt" "activityLibraryEnabled"
grep_file "$ANDROID/data/db/entities/RecordingSessionEntity.kt" "activityType"
grep_file "$ANDROID/ui/playback/SessionListScreen.kt" "ActivityTypeFilterRow"
grep_file "$ANDROID/ui/dashboard/DashboardScreen.kt" "HomeQuickStatsStrip"
grep_file "$ROOT/project.config.json" '"v2_activity_library": true'

if [ "$FAIL" -ne 0 ]; then
  echo "v2 activity library gate FAILED"
  exit 1
fi
echo "v2 activity library gate passed"
