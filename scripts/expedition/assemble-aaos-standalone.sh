#!/usr/bin/env bash
# Assemble the AAOS sideload APK (distinct applicationId + automotive required).
# Usage: bash scripts/expedition/assemble-aaos-standalone.sh [debug|release]
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
KIND="${1:-debug}"
case "$KIND" in
  debug) TASK="assembleDebug" ;;
  release) TASK="assembleRelease" ;;
  *) echo "usage: $0 [debug|release]" >&2; exit 2 ;;
esac
cd "$ROOT/examples/android"
chmod +x gradlew
./gradlew "$TASK" --no-daemon -PaaosStandalone=true
