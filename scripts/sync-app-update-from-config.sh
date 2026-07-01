#!/usr/bin/env bash
# Sync gitignored app-update assets from project.config.json releaseRepo.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [ ! -f project.config.json ]; then
  echo "MISSING project.config.json"
  exit 1
fi

REPO="$(python3 - <<'PY'
import json
from pathlib import Path
cfg = json.loads(Path("project.config.json").read_text(encoding="utf-8"))
repo = (cfg.get("releaseRepo") or "").strip()
repo = repo.replace("https://github.com/", "").replace("github.com/", "")
if repo.count("/") != 1:
    raise SystemExit(f"invalid releaseRepo: {repo!r}")
print(repo)
PY
)"

DONATIONS="$(python3 - <<'PY'
import json
from pathlib import Path
cfg = json.loads(Path("project.config.json").read_text(encoding="utf-8"))
print((cfg.get("donationsUrl") or "").strip())
PY
)"

python3 scripts/sync-stack-config.py "$ROOT" "$REPO" "$DONATIONS"

ASSET="$ROOT/examples/android/app/src/main/assets/app-update.json"
if [ ! -f "$ASSET" ]; then
  echo "MISSING $ASSET after sync"
  exit 1
fi

python3 - <<'PY'
import json
from pathlib import Path
data = json.loads(Path("examples/android/app/src/main/assets/app-update.json").read_text(encoding="utf-8"))
repo = data.get("release_repo", "")
if not repo or "OWNER" in repo.upper():
    raise SystemExit(f"invalid release_repo in asset: {repo!r}")
print(f"OK app-update.json release_repo={repo}")
PY
