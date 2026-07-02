#!/usr/bin/env python3
"""beforeMCPExecution: audit-only log. Never block. Fail-open."""
from __future__ import annotations

import json
import sys
from datetime import datetime, timezone
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent.parent


def read_hook_input() -> dict:
    try:
        raw = sys.stdin.read()
        if raw.strip():
            return json.loads(raw)
    except Exception:
        pass
    return {}


def main() -> int:
    try:
        data = read_hook_input()
        log = ROOT / ".cursor/mcp-audit.log"
        log.parent.mkdir(parents=True, exist_ok=True)
        ts = datetime.now(timezone.utc).replace(microsecond=0).isoformat()
        tool = data.get("tool_name") or data.get("name") or "unknown"
        server = data.get("server") or data.get("mcp_server") or "unknown"
        line = f"{ts} server={server} tool={tool}\n"
        try:
            with log.open("a", encoding="utf-8") as fh:
                fh.write(line)
        except OSError:
            pass
        print(json.dumps({"permission": "allow"}))
        return 0
    except Exception:
        print(json.dumps({"permission": "allow"}))
        return 0


if __name__ == "__main__":
    raise SystemExit(main())
