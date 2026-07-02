#!/usr/bin/env python3
"""Run repo scripts without .sh paths in agent shell command strings."""
from __future__ import annotations

import argparse
import shutil
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SCRIPTS = ROOT / "scripts"


def script_argv(script: Path) -> str:
    return script.relative_to(ROOT).as_posix()


def discover_scripts() -> list[str]:
    names: set[str] = set()
    for ext in (".sh", ".ps1"):
        for path in sorted(SCRIPTS.glob(f"*{ext}")):
            if path.is_file():
                names.add(path.stem)
    return sorted(names)


def resolve_script(name: str) -> Path | None:
    for ext in (".sh", ".ps1"):
        path = SCRIPTS / f"{name}{ext}"
        if path.is_file():
            return path
    return None


def run_script(name: str, args: list[str]) -> int:
    script = resolve_script(name)
    if script is None:
        print(f"ERROR: unknown script '{name}'", file=sys.stderr)
        print("Run: python3 scripts/agent-run.py --list", file=sys.stderr)
        return 1
    if script.suffix == ".ps1":
        return subprocess.run(
            ["powershell", "-NoProfile", "-File", str(script), *args],
            cwd=ROOT,
            check=False,
        ).returncode
    bash = shutil.which("bash")
    if not bash:
        print("ERROR: bash not found on PATH.", file=sys.stderr)
        print(
            f"Install Git Bash or run: powershell -NoProfile -File {script_argv(script)}",
            file=sys.stderr,
        )
        return 1
    return subprocess.run(
        [bash, script_argv(script), *args],
        cwd=ROOT,
        check=False,
    ).returncode


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(
        description="Dispatch scripts/{name}.sh|.ps1 without .sh in agent command strings.",
    )
    parser.add_argument("--list", action="store_true", help="List available script names")
    parser.add_argument("name", nargs="?", help="Script basename without extension")
    parser.add_argument("args", nargs=argparse.REMAINDER, help="Arguments passed to the script")
    ns = parser.parse_args(argv)

    if ns.list or not ns.name:
        for name in discover_scripts():
            print(name)
        return 0 if ns.list else parser.print_help() or 1

    args = list(ns.args)
    if args and args[0] == "--":
        args = args[1:]
    return run_script(ns.name, args)


if __name__ == "__main__":
    raise SystemExit(main())
