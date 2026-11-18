#!/usr/bin/env bash
# Validate F-Droid/Fastlane metadata scaffold (AGENT gate; APK hashes remain ADB).
# Usage: scripts/verify-fdroid-metadata.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

META="$ROOT/examples/android/metadata/en-US"
GRADLE="$ROOT/examples/android/app/build.gradle.kts"
ERRORS=0

fail() {
  echo "FAIL: $1"
  ERRORS=$((ERRORS + 1))
}

ok() {
  echo "OK   $1"
}

if [ ! -d "$META" ]; then
  fail "missing $META"
else
  ok "metadata directory present"
fi

for f in title.txt short_description.txt full_description.txt license.txt source_code.txt; do
  if [ ! -s "$META/$f" ]; then
    fail "missing or empty $META/$f"
  else
    ok "$f present"
  fi
done

if [ -f "$ROOT/examples/android/metadata/REPRODUCIBLE.md" ]; then
  ok "REPRODUCIBLE.md present"
else
  fail "missing examples/android/metadata/REPRODUCIBLE.md"
fi

if [ -s "$META/AntiFeatures.txt" ]; then
  ok "AntiFeatures.txt present"
else
  fail "missing or empty $META/AntiFeatures.txt"
fi

FASTLANE="$ROOT/fastlane/metadata/android/en-US"
if [ -s "$FASTLANE/AntiFeatures.txt" ]; then
  ok "fastlane AntiFeatures.txt present"
else
  fail "missing or empty $FASTLANE/AntiFeatures.txt"
fi
for f in title.txt short_description.txt full_description.txt; do
  if [ ! -s "$FASTLANE/$f" ]; then
    fail "missing or empty $FASTLANE/$f"
  else
    ok "fastlane $f present"
  fi
done
if [ ! -s "$FASTLANE/images/icon.png" ]; then
  fail "missing $FASTLANE/images/icon.png"
else
  ok "fastlane icon.png present"
fi
if ! ls "$FASTLANE/images/phoneScreenshots"/*.png >/dev/null 2>&1; then
  fail "missing fastlane phoneScreenshots PNGs"
else
  ok "fastlane phoneScreenshots present"
fi

if [ ! -f "$GRADLE" ]; then
  fail "missing $GRADLE"
else
  VERSION_CODE="$(grep -E 'versionCode\s*=' "$GRADLE" | head -1 | sed -E 's/.*=\s*([0-9]+).*/\1/')"
  if [ -z "${VERSION_CODE:-}" ]; then
    fail "could not parse versionCode from build.gradle.kts"
  elif [ ! -s "$META/changelogs/${VERSION_CODE}.txt" ]; then
    fail "missing changelog $META/changelogs/${VERSION_CODE}.txt"
  else
    ok "changelog for versionCode ${VERSION_CODE}"
    NEXT_CODE=$((VERSION_CODE + 1))
    if [ -s "$META/changelogs/${NEXT_CODE}.txt" ]; then
      ok "next changelog ${NEXT_CODE}"
    else
      fail "missing next changelog $META/changelogs/${NEXT_CODE}.txt"
    fi
    if [ -s "$FASTLANE/changelogs/${VERSION_CODE}.txt" ]; then
      ok "fastlane changelog ${VERSION_CODE}"
    else
      fail "missing $FASTLANE/changelogs/${VERSION_CODE}.txt"
    fi
  fi
fi

if [ ! -d "$META/images" ]; then
  fail "missing $META/images/ (add README + assets before submit)"
else
  ok "images directory present"
fi

if [ -f "$ROOT/LICENSE" ]; then
  ok "root LICENSE present (MIT for template)"
else
  fail "missing root LICENSE"
fi

if [ -d "$FASTLANE" ]; then
  ok "root fastlane metadata present"
else
  fail "missing $FASTLANE"
fi

echo ""
echo "SKIP [ADB] reproducible APK hash verification — run on device/emulator per modules/android/MODULE.md"

# Dummy/placeholder screenshots fail even when metadata text is present.
if ! bash "$ROOT/scripts/check-fdroid-screenshots.sh"; then
  fail "dummy F-Droid screenshots are not allowed"
fi

if [ "$ERRORS" -gt 0 ]; then
  echo "${ERRORS} F-Droid metadata check(s) failed"
  exit 1
fi

echo "F-Droid metadata scaffold verified"
