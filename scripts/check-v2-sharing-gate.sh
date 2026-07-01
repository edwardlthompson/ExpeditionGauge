#!/usr/bin/env bash
# Sharing polish gate (Sprint 27+).
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

echo "=== v2 sharing gate (Sprint 27+) ==="

check_file "$DOCS/sharing-polish.md"
check_file "$ANDROID/share/ShareCardGenerator.kt"
check_file "$ANDROID/share/ShareExportLauncher.kt"
check_file "$ANDROID/ui/share/SharePreviewSheet.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/share/ShareCardGeneratorTest.kt"

grep_file "$ANDROID/FeatureFlags.kt" "sharingPolishEnabled"
grep_file "$ANDROID/ui/navigation/AppScreenPlaybackRoute.kt" "SharePreviewSheet"
grep_file "$ROOT/project.config.json" '"v2_sharing_polish": true'

if [ "$FAIL" -ne 0 ]; then
  echo "v2 sharing gate FAILED"
  exit 1
fi
echo "v2 sharing gate passed"
