#!/usr/bin/env bash
# Elevation profile gate (Sprint 23+).
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

echo "=== v2 elevation gate (Sprint 23+) ==="

check_file "$DOCS/elevation-profile.md"
check_file "$ANDROID/playback/ElevationProfileBuilder.kt"
check_file "$ANDROID/ui/playback/ElevationProfilePanel.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/playback/ElevationProfileBuilderTest.kt"

grep_file "$ANDROID/FeatureFlags.kt" "elevationProfileEnabled"
grep_file "$ANDROID/ui/playback/PlaybackScreenContent.kt" "ElevationProfilePanel"
grep_file "$ROOT/project.config.json" '"v2_elevation_profile": true'
grep_file "$ROOT/examples/android/app/src/main/res/values/strings_playback.xml" "playback_elevation_stats"

if [ "$FAIL" -ne 0 ]; then
  echo "v2 elevation gate FAILED"
  exit 1
fi
echo "v2 elevation gate passed"
