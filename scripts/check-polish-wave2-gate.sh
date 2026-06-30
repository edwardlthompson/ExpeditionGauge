#!/usr/bin/env bash
# Polish wave 2 gate (Sprint 15+).
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

echo "=== Polish wave 2 gate (Sprint 15+) ==="

check_file "$DOCS/dashboard-presets.md"
check_file "$ROOT/docs/adr/0004-dashboard-presets.md"
check_file "$ANDROID/presets/DashboardPreset.kt"
check_file "$ANDROID/settings/SettingsProfile.kt"
check_file "$ANDROID/settings/SettingsProfileRepository.kt"
check_file "$ANDROID/ui/dashboard/PresetSwitcherChip.kt"
check_file "$ANDROID/ui/settings/SettingsPresetOptions.kt"
check_file "$DOCS/playback-layout.md"
check_file "$ANDROID/playback/PlaybackLayoutState.kt"
check_file "$ANDROID/playback/PlaybackInputHandler.kt"
check_file "$ANDROID/ui/playback/PlaybackLayoutControls.kt"

if [ "$FAIL" -ne 0 ]; then
  echo "Polish wave 2 gate FAILED"
  exit 1
fi
echo "Polish wave 2 gate passed"
