#!/usr/bin/env bash
# Dispatch BUILD_PLAN Parallel lane agents (wrapper).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
SPRINT=""
EXTRA=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    -Sprint|--sprint) SPRINT="$2"; shift 2 ;;
    *) EXTRA+=("$1"); shift ;;
  esac
done
if [[ -z "$SPRINT" ]]; then
  echo "Usage: bash scripts/expedition/dispatch-parallel-agents.sh -Sprint 19b [--] [pwsh flags]" >&2
  exit 1
fi
if command -v pwsh >/dev/null 2>&1; then
  pwsh "$ROOT/scripts/expedition/dispatch-parallel-agents.ps1" -Sprint "$SPRINT" "${EXTRA[@]}"
else
  powershell -NoProfile -File "$ROOT/scripts/expedition/dispatch-parallel-agents.ps1" -Sprint "$SPRINT" "${EXTRA[@]}"
fi
