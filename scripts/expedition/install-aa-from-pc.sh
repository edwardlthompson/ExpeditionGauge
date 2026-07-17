#!/usr/bin/env bash
# Install ExpeditionGauge for Android Auto (PC + rooted phone).
# Thin wrapper around aa-spoof-adb.sh (Play Store installer + initiator spoof).
#
# Usage:
#   bash install-aa-from-pc.sh ExpeditionGauge-2.16.2.apk
#   SERIAL=8bf09993 bash install-aa-from-pc.sh ExpeditionGauge-2.16.2.apk
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
exec bash "$HERE/aa-spoof-adb.sh" "$@"
