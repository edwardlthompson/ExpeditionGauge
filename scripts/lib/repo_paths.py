"""Normalize repo paths across Git Bash (MSYS) and native Windows Python."""
from __future__ import annotations

from pathlib import Path


def resolve_repo_root(raw: str | Path) -> Path:
    """Resolve a repo root path from shell or CLI.

    Git Bash ``pwd`` yields ``/c/Users/...``. Passing that string to Windows
    CPython becomes ``\\c\\Users\\...`` and breaks open/mkdir. Convert the
    MSYS drive form to ``C:/Users/...`` first.
    """
    s = str(raw).strip().replace("\\", "/")
    if len(s) >= 3 and s[0] == "/" and s[2] == "/" and s[1].isalpha():
        s = f"{s[1].upper()}:{s[2:]}"
    return Path(s).expanduser().resolve()
