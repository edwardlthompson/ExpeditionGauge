#!/usr/bin/env bash
# Unit tests for bootstrap lifecycle engine (stdlib unittest).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
# shellcheck source=lib/resolve-python.sh
source "$ROOT/scripts/lib/resolve-python.sh"

export PYTHONPATH="$ROOT/scripts/lib${PYTHONPATH:+:$PYTHONPATH}"
if "$PY" -c "from pathlib import Path; from build_sprint_model import is_template_repo; raise SystemExit(0 if is_template_repo(Path('.')) else 1)"; then
  exec "$PY" -m unittest discover -s tests -p "test_*.py" -q
fi
echo "SKIP bootstrap-engine unit tests (child repo)"
exit 0
