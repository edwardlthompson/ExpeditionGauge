#!/usr/bin/env bash
# Install ExpeditionGauge for Android Auto (PC + rooted phone).
# Sets installerPackageName AND initiatingPackageName to com.android.vending.
#
# Usage:
#   bash install-aa-from-pc.sh ExpeditionGauge-2.14.1.apk
#   SERIAL=b5214fc6 bash install-aa-from-pc.sh ExpeditionGauge-2.14.1.apk
set -euo pipefail

PLAY=com.android.vending
PKG=dev.foss.expeditiongauge
APK="${1:-}"
ADB=(adb)
if [[ -n "${SERIAL:-}" ]]; then ADB=(adb -s "$SERIAL"); fi

if [[ -z "$APK" ]]; then
  APK=$(ls -t ExpeditionGauge-*.apk 2>/dev/null | head -1 || true)
fi
if [[ -z "$APK" || ! -f "$APK" ]]; then
  echo "Usage: $0 ExpeditionGauge-X.Y.Z.apk" >&2
  exit 1
fi

if ! command -v adb >/dev/null; then
  echo "adb not found" >&2
  exit 1
fi

"${ADB[@]}" root >/dev/null
sleep 1
if ! "${ADB[@]}" shell id | grep -q 'uid=0'; then
  cat >&2 <<'EOF'
Device is not rooted (adb root failed).

Unrooted: see ANDROID_AUTO_SIDELOAD.md — KingInstaller (Android <=13) or Magisk (14+).
EOF
  exit 1
fi

REMOTE=/data/local/tmp/ExpeditionGauge-aa-install.apk
"${ADB[@]}" push "$APK" "$REMOTE" >/dev/null
UID_PLAY=$("${ADB[@]}" shell dumpsys package "$PLAY" | sed -n 's/.*userId=\([0-9]*\).*/\1/p' | head -1)
SIZE=$("${ADB[@]}" shell stat -c %s "$REMOTE" | tr -d '\r')
CREATE=$("${ADB[@]}" shell "su $UID_PLAY -c \"pm install-create --user 0 -i $PLAY -r -S $SIZE\"" | tr -d '\r')
SID=$(echo "$CREATE" | sed -n 's/.*\[\([0-9]*\)\].*/\1/p')
if [[ -z "$SID" ]]; then
  echo "install-create failed: $CREATE" >&2
  exit 1
fi
echo "Session $SID as uid $UID_PLAY ($PLAY)"
"${ADB[@]}" shell "pm install-write -S $SIZE $SID base $REMOTE" >/dev/null
"${ADB[@]}" shell "pm install-commit $SID" >/dev/null
"${ADB[@]}" shell "rm -f $REMOTE" >/dev/null
"${ADB[@]}" shell "am start -n $PKG/.MainActivity" >/dev/null || true

DUMP=$("${ADB[@]}" shell dumpsys package "$PKG")
echo "$DUMP" | grep -E 'installerPackageName|initiatingPackageName' || true
echo "$DUMP" | grep -q "installerPackageName=$PLAY" || { echo "installer attribution failed" >&2; exit 1; }
echo "$DUMP" | grep -q "initiatingPackageName=$PLAY" || { echo "initiator attribution failed" >&2; exit 1; }

cat <<EOF

OK — Play Store attribution set.

On the phone: AA developer mode → Unknown sources → Customize launcher → enable ExpeditionGauge → USB to car.
EOF
