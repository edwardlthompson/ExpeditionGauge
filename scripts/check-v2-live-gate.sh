#!/usr/bin/env bash
# v2 live telemetry gate (Sprint 19+).
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

echo "=== v2 live gate (Sprint 19+) ==="

check_file "$DOCS/live-telemetry.md"
check_file "$ROOT/docs/adr/0006-live-telemetry.md"
check_file "$ROOT/signaling-server/server.js"
check_file "$ROOT/live-receiver/index.html"
check_file "$ROOT/live-receiver/app.js"
check_file "$ANDROID/live/LiveTelemetrySender.kt"
check_file "$ANDROID/live/LiveTelemetryReceiver.kt"
check_file "$ANDROID/live/LiveWebSocketClient.kt"
check_file "$ANDROID/live/LivePairingManager.kt"
check_file "$ANDROID/live/LiveTelemetryEncoder.kt"
check_file "$ANDROID/ui/live/LivePairingSheet.kt"
check_file "$ANDROID/ui/live/LiveReceiverScreen.kt"

if [ "$FAIL" -ne 0 ]; then
  echo "v2 live gate FAILED"
  exit 1
fi
echo "v2 live gate passed"
