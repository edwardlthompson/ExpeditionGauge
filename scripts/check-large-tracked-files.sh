#!/usr/bin/env bash
# Fail if any tracked file exceeds size budget (matches pre-commit 500KB gate)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

MAX_KB=500
MAX_BYTES=$((MAX_KB * 1024))
ERRORS=0
MAX_REPORT=20
reported=0

# shellcheck source=lib/resolve-python.sh
. "$(cd "$(dirname "$0")" && pwd)/lib/resolve-python.sh"

"$PY" - "$MAX_KB" <<'PY'
import os
import subprocess
import sys

max_kb = int(sys.argv[1])
max_bytes = max_kb * 1024
max_report = 20
errors = 0
reported = 0

try:
    files = subprocess.check_output(["git", "ls-files"], text=True, errors="replace").splitlines()
except Exception as e:
    print(f"ERROR running git ls-files: {e}", file=sys.stderr)
    sys.exit(1)

for file in files:
    if not file:
        continue
    size = 0
    if os.path.isfile(file):
        try:
            size = os.path.getsize(file)
        except OSError:
            pass
    if size > max_bytes:
        kb = size // 1024
        print(f"LARGE TRACKED FILE: {file} ({kb} KB > {max_kb} KB)")
        errors += 1
        reported += 1
        if reported >= max_report:
            print(f"... truncated (max {max_report})")
            break

if errors > 0:
    print(f"{errors} tracked file(s) exceed {max_kb} KB")
    sys.exit(1)

print("Large tracked file check passed")
PY
