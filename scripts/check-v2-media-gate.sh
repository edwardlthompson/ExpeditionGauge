#!/usr/bin/env bash
# Media attachments gate (Sprint 22+).
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

echo "=== v2 media gate (Sprint 22+) ==="

check_file "$DOCS/media-attachments.md"
check_file "$ROOT/docs/adr/0011-media-attachments.md"
check_file "$ANDROID/data/db/entities/SessionMediaEntity.kt"
check_file "$ANDROID/media/SessionMediaRepository.kt"
check_file "$ANDROID/media/SessionDeleteService.kt"
check_file "$ANDROID/ui/playback/MediaViewerSheet.kt"
check_file "$ANDROID/ui/settings/SettingsMediaOptions.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/media/SessionMediaMarkerTest.kt"

grep_file "$ROOT/examples/android/app/src/main/java/dev/foss/expeditiongauge/data/db/ExpeditionGaugeDatabase.kt" "version = 4"
grep_file "$ROOT/examples/android/app/src/main/java/dev/foss/expeditiongauge/playback/PlaybackModels.kt" "MEDIA_ATTACHMENT"
grep_file "$ANDROID/FeatureFlags.kt" "mediaAttachmentsEnabled"

if [ "$FAIL" -ne 0 ]; then
  echo "v2 media gate FAILED"
  exit 1
fi
echo "v2 media gate passed"
