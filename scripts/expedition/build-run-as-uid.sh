#!/usr/bin/env bash
# Cross-compile scripts/expedition/bin/run-as-uid-arm64 with the Android NDK.
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
SRC="$HERE/bin/run-as-uid.c"
OUT="$HERE/bin/run-as-uid-arm64"

if [[ ! -f "$SRC" ]]; then
  echo "FAIL: missing $SRC" >&2
  exit 1
fi
if [[ -f "$OUT" && "$OUT" -nt "$SRC" && "${FORCE_REBUILD:-}" != "1" ]]; then
  echo "OK   $OUT (up to date)"
  exit 0
fi

ndk="${ANDROID_NDK_HOME:-${ANDROID_NDK_ROOT:-}}"
if [[ -z "$ndk" || ! -d "$ndk" ]]; then
  for sdk in "${ANDROID_SDK_ROOT:-}" "${ANDROID_HOME:-}"; do
    [[ -n "$sdk" && -d "$sdk/ndk" ]] || continue
    ndk=$(ls -d "$sdk/ndk"/* 2>/dev/null | sort -V | tail -1 || true)
    [[ -n "$ndk" ]] && break
  done
fi
if [[ -z "${ndk:-}" || ! -d "$ndk" ]]; then
  echo "FAIL: set ANDROID_NDK_HOME or install an NDK under ANDROID_SDK_ROOT/ndk" >&2
  exit 1
fi

host=""
case "$(uname -s)" in
  Linux) host="linux-x86_64" ;;
  Darwin)
    if [[ "$(uname -m)" == "arm64" ]]; then host="darwin-arm64"; else host="darwin-x86_64"; fi
    ;;
  MINGW*|MSYS*|CYGWIN*) host="windows-x86_64" ;;
  *) echo "FAIL: unsupported host $(uname -s)" >&2; exit 1 ;;
esac

clang="$ndk/toolchains/llvm/prebuilt/$host/bin/aarch64-linux-android21-clang"
if [[ "$host" == windows-x86_64 ]]; then
  [[ -x "$clang" ]] || clang="${clang}.cmd"
fi
if [[ ! -x "$clang" && ! -f "$clang" ]]; then
  echo "FAIL: NDK clang not found at $clang" >&2
  exit 1
fi

mkdir -p "$HERE/bin"
"$clang" -O2 -fPIE -pie -s -o "$OUT" "$SRC"
chmod 755 "$OUT"
echo "OK   built $OUT"
