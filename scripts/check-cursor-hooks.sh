#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
# shellcheck source=lib/repo-root.sh
source "$ROOT/scripts/lib/repo-root.sh"
ARGS=(--root .)
while [ $# -gt 0 ]; do
  case "$1" in
    --smoke) ARGS+=(--smoke); shift ;;
    *) shift ;;
  esac
done
python3 scripts/lib/check_cursor_hooks.py "${ARGS[@]}"
