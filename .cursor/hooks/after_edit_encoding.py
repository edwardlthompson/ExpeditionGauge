#!/usr/bin/env python3
"""afterFileEdit: UTF-8 encoding check on edited text files. Fail-open."""
from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent.parent

TEXT_EXT = {
    ".md",
    ".json",
    ".yml",
    ".yaml",
    ".sh",
    ".ps1",
    ".mdc",
    ".ts",
    ".tsx",
    ".py",
    ".toml",
    ".html",
    ".css",
    ".txt",
    ".xml",
    ".gradle",
    ".kts",
    ".kt",
    ".java",
}


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
        path_str = data.get("file_path") or data.get("path") or ""
        if not path_str:
            return 0
        path = ROOT / path_str
        if not path.is_file():
            return 0
        if path.suffix.lower() not in TEXT_EXT and path.name not in (
            ".gitignore",
            "LICENSE",
            "AGENTS.md",
        ):
            return 0
        proc = subprocess.run(
            ["python3", str(ROOT / "scripts/check-file-encoding.py"), str(path)],
            capture_output=True,
            text=True,
            timeout=5,
            cwd=ROOT,
            check=False,
        )
        if proc.returncode != 0:
            msg = (proc.stderr or proc.stdout or "encoding check failed").strip()[:200]
            print(json.dumps({"additional_context": f"Encoding check failed for {path_str}: {msg}"}))
    except Exception:
        pass
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
