#!/usr/bin/env bash
# Count open Critical/High Dependabot alerts (paginated).
# Usage: scripts/count-critical-high-dependabot.sh
# Exit 0 prints count to stdout; exit 1 on API/auth error.
set -euo pipefail

if ! command -v gh >/dev/null 2>&1; then
  echo "ERROR: gh CLI required" >&2
  exit 1
fi

REPO="${GITHUB_REPO:-$(gh repo view --json nameWithOwner -q .nameWithOwner 2>/dev/null || true)}"
if [ -z "$REPO" ]; then
  echo "ERROR: gh auth required" >&2
  exit 1
fi

if command -v python3 >/dev/null 2>&1; then PY=python3
elif command -v python >/dev/null 2>&1; then PY=python
else PY=python3; fi

COUNT="$("$PY" - "$REPO" << 'PY'
import json, subprocess, sys

repo = sys.argv[1]
# Dependabot alerts reject ?page=; use gh --paginate (Link header).
url = f"repos/{repo}/dependabot/alerts?state=open&per_page=100"
proc = subprocess.run(
    ["gh", "api", "--paginate", url],
    capture_output=True, text=True,
)
if proc.returncode != 0:
    print(proc.stderr or proc.stdout or "error", file=sys.stderr)
    raise SystemExit(1)
raw = (proc.stdout or "").strip()
if not raw:
    print(0)
    raise SystemExit(0)
# --paginate may concatenate JSON arrays; prefer NDJSON-safe parse.
alerts: list = []
try:
    parsed = json.loads(raw)
    if isinstance(parsed, list):
        alerts = parsed
    else:
        print("error: unexpected Dependabot API payload", file=sys.stderr)
        raise SystemExit(1)
except json.JSONDecodeError:
    # Concatenated arrays: ][
    fixed = raw.replace("][", "],[")
    try:
        parsed = json.loads(f"[{fixed}]" if not fixed.startswith("[") else fixed)
    except json.JSONDecodeError:
        # Fall back: wrap as array-of-arrays
        chunks = []
        for part in raw.replace("]\n[", "]\n[").split("\n"):
            part = part.strip()
            if not part:
                continue
            try:
                chunk = json.loads(part)
                if isinstance(chunk, list):
                    chunks.extend(chunk)
            except json.JSONDecodeError:
                pass
        if not chunks and "][" in raw:
            import re
            for m in re.finditer(r"\[.*?\]", raw, flags=re.S):
                chunks.extend(json.loads(m.group(0)))
        alerts = chunks
    else:
        if isinstance(parsed, list) and parsed and isinstance(parsed[0], list):
            for chunk in parsed:
                alerts.extend(chunk)
        elif isinstance(parsed, list):
            alerts = parsed

total = 0
for a in alerts:
    if not isinstance(a, dict):
        continue
    sev = (a.get("security_vulnerability") or {}).get("severity", "").lower()
    if not sev:
        sev = (a.get("security_advisory") or {}).get("severity", "").lower()
    if sev in ("critical", "high"):
        total += 1
print(total)
PY
)"

echo "$COUNT"
