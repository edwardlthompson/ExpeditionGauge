#!/usr/bin/env bash
# Copy-paste Play Store attribution install for ExpeditionGauge (Android Auto).
# Requires: adb, USB debugging, rooted phone (Magisk su OR adb root + run-as-uid-arm64).
#
# Usage (from the AA-install-kit folder, or any folder with the APK):
#   bash aa-spoof-adb.sh
#   bash aa-spoof-adb.sh ExpeditionGauge-2.16.2.apk
#   SERIAL=8bf09993 bash aa-spoof-adb.sh ExpeditionGauge-2.16.2.apk
set -euo pipefail

PLAY=com.android.vending
PKG=dev.foss.expeditiongauge
HERE="$(cd "$(dirname "$0")" && pwd)"
APK="${1:-}"
ADB=(adb)
if [[ -n "${SERIAL:-}" ]]; then ADB=(adb -s "$SERIAL"); fi

if [[ -z "$APK" ]]; then
  APK=$(ls -t "$HERE"/ExpeditionGauge-*.apk 2>/dev/null | head -1 || true)
fi
if [[ -z "$APK" || ! -f "$APK" ]]; then
  echo "Usage: $0 ExpeditionGauge-X.Y.Z.apk" >&2
  exit 1
fi

"${ADB[@]}" root >/dev/null || true
sleep 1
if ! "${ADB[@]}" shell id | grep -q 'uid=0'; then
  echo "FAIL: adb root required (Magisk / engineering build)." >&2
  exit 1
fi

REMOTE=/data/local/tmp/ExpeditionGauge-aa-install.apk
"${ADB[@]}" push "$APK" "$REMOTE" >/dev/null

UID_PLAY=$("${ADB[@]}" shell cmd package list packages -U "$PLAY" | sed -n 's/.*uid:\([0-9]*\).*/\1/p' | head -1 | tr -d '\r')
if [[ -z "$UID_PLAY" ]]; then
  UID_PLAY=$("${ADB[@]}" shell dumpsys package "$PLAY" | sed -n 's/.*\(userId\|appId\)=\([0-9]*\).*/\2/p' | head -1 | tr -d '\r')
fi
if [[ -z "$UID_PLAY" ]]; then
  echo "FAIL: Play Store ($PLAY) uid not found" >&2
  exit 1
fi

SIZE=$("${ADB[@]}" shell stat -c %s "$REMOTE" | tr -d '\r')
CREATE_CMD="pm install-create --user 0 -i $PLAY -r -S $SIZE"
CREATE=$("${ADB[@]}" shell "su $UID_PLAY -c \"$CREATE_CMD\"" 2>&1 | tr -d '\r' || true)

NEED_HELPER=0
if [[ "$CREATE" == *"su: inaccessible"* || "$CREATE" == *"not found"* || "$CREATE" == *"No such file"* ]]; then
  NEED_HELPER=1
fi
if [[ ! "$CREATE" =~ \[[0-9]+\] ]]; then
  NEED_HELPER=1
fi

if [[ "$NEED_HELPER" -eq 1 ]]; then
  HELPER="$HERE/bin/run-as-uid-arm64"
  if [[ ! -f "$HELPER" ]]; then
    echo "FAIL: Magisk su missing and no bin/run-as-uid-arm64 next to this script." >&2
    exit 1
  fi
  echo "Magisk su missing — using run-as-uid-arm64 helper"
  "${ADB[@]}" push "$HELPER" /data/local/tmp/run-as-uid >/dev/null
  "${ADB[@]}" shell "chmod 755 /data/local/tmp/run-as-uid" >/dev/null
  CREATE=$("${ADB[@]}" shell "/data/local/tmp/run-as-uid $UID_PLAY $CREATE_CMD" | tr -d '\r')
fi

SID=$(echo "$CREATE" | sed -n 's/.*\[\([0-9]*\)\].*/\1/p')
if [[ -z "$SID" ]]; then
  echo "FAIL: install-create: $CREATE" >&2
  exit 1
fi

echo "Session $SID as uid $UID_PLAY ($PLAY)"
"${ADB[@]}" shell "pm install-write -S $SIZE $SID base $REMOTE" >/dev/null
"${ADB[@]}" shell "pm install-commit $SID" >/dev/null
"${ADB[@]}" shell "rm -f $REMOTE" >/dev/null
"${ADB[@]}" shell "am force-stop com.google.android.projection.gearhead" >/dev/null || true
"${ADB[@]}" shell "am start -n $PKG/.MainActivity" >/dev/null || true

DUMP=$("${ADB[@]}" shell dumpsys package "$PKG")
echo "$DUMP" | grep -E 'installerPackageName|initiatingPackageName' || true
echo "$DUMP" | grep -q "installerPackageName=$PLAY" || { echo "FAIL: installer attribution" >&2; exit 1; }
echo "$DUMP" | grep -q "initiatingPackageName=$PLAY" || { echo "FAIL: initiator attribution" >&2; exit 1; }

cat <<EOF

OK — Play Store spoof install complete.

On the phone:
  1. Android Auto → tap version ~10x → Developer settings → Unknown sources ON
  2. Customize launcher → enable ExpeditionGauge
  3. USB reconnect to the head unit
EOF
