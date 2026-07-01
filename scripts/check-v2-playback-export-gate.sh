#!/usr/bin/env bash
# Playback video export gate (Sprint 25+).
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

echo "=== v2 playback export gate (Sprint 25+) ==="

check_file "$DOCS/playback-video-export.md"
check_file "$ROOT/docs/adr/0012-playback-video-export.md"
check_file "$ANDROID/export/PlaybackVideoExporter.kt"
check_file "$ANDROID/export/VideoFrameCapturer.kt"
check_file "$ANDROID/export/PlaybackVideoExportWorker.kt"
check_file "$ANDROID/ui/playback/PlaybackExportPanel.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/export/VideoFrameCapturerTest.kt"

grep_file "$ANDROID/FeatureFlags.kt" "playbackVideoExportEnabled"
grep_file "$ANDROID/ui/playback/PlaybackScreen.kt" "PlaybackExportPanel"
grep_file "$ROOT/project.config.json" '"v2_playback_export": true'
grep_file "$ROOT/examples/android/app/build.gradle.kts" "work-runtime-ktx"

if [ "$FAIL" -ne 0 ]; then
  echo "v2 playback export gate FAILED"
  exit 1
fi
echo "v2 playback export gate passed"
