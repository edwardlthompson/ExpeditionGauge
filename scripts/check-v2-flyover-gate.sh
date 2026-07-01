#!/usr/bin/env bash
# 3D flyover gate (Sprint 26+).
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

echo "=== v2 flyover gate (Sprint 26+) ==="

check_file "$DOCS/3d-flyover.md"
check_file "$ROOT/docs/design/maplibre-3d-terrain.md"
check_file "$ANDROID/flyover/FlyoverCameraPath.kt"
check_file "$ANDROID/flyover/MapLibreFlyoverRenderer.kt"
check_file "$ANDROID/flyover/FlyoverVideoExportWorker.kt"
check_file "$ANDROID/ui/playback/FlyoverExportPanel.kt"
check_file "$ROOT/examples/android/app/src/test/java/dev/foss/expeditiongauge/flyover/FlyoverCameraPathTest.kt"

grep_file "$ANDROID/FeatureFlags.kt" "flyover3dEnabled"
grep_file "$ANDROID/ui/playback/PlaybackScreen.kt" "FlyoverExportPanel"
grep_file "$ANDROID/flyover/FlyoverThermalGuard.kt" "ThermalStatus"
grep_file "$ROOT/project.config.json" '"v2_3d_flyover": true'

if [ "$FAIL" -ne 0 ]; then
  echo "v2 flyover gate FAILED"
  exit 1
fi
echo "v2 flyover gate passed"
