#!/usr/bin/env python3
"""Build slim CC0 OBDex DTC catalog (code -> English title) for app assets.

Source: https://github.com/foerbsnavi/OBDex (LICENSE-DATA: CC0-1.0)
Pages CDN may 404; we parse data/generic/*_enriched.yaml from GitHub raw.

Usage:
  python3 scripts/expedition/fetch-obdex-dtc.py
"""
from __future__ import annotations

import gzip
import json
import re
import sys
import urllib.request
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
OUT = REPO_ROOT / "examples/android/app/src/main/assets/dtc/obdex_en.gz"
BASE = "https://raw.githubusercontent.com/foerbsnavi/OBDex/main/data/generic"
FILES = [
    "P0xxx_enriched.yaml",
    "P2xxx_enriched.yaml",
    "P3xxx_enriched.yaml",
    "B0xxx_enriched.yaml",
    "C0xxx_enriched.yaml",
    "U0xxx_enriched.yaml",
    "U3xxx_enriched.yaml",
]
CODE_RE = re.compile(r"^[PCBU][0-9A-Fa-f]{4}$")
TITLE_EN_RE = re.compile(r"(?m)^  title:\n    en: (.+)$")


def fetch(url: str) -> str:
    req = urllib.request.Request(url, headers={"User-Agent": "ExpeditionGauge"})
    with urllib.request.urlopen(req, timeout=180) as resp:
        return resp.read().decode("utf-8")


def parse_titles(text: str) -> dict[str, str]:
    out: dict[str, str] = {}
    parts = re.split(r"(?m)^- code: ", text)
    for part in parts[1:]:
        code = part.split(None, 1)[0].strip()
        if not CODE_RE.fullmatch(code):
            continue
        m = TITLE_EN_RE.search(part)
        if not m:
            continue
        title = m.group(1).strip().strip('"').strip("'")
        if title:
            out[code.upper()] = title
    return out


def main() -> int:
    merged: dict[str, str] = {}
    for name in FILES:
        url = f"{BASE}/{name}"
        print(f"fetch {name}", flush=True)
        chunk = parse_titles(fetch(url))
        merged.update(chunk)
        print(f"  +{len(chunk)} (total {len(merged)})", flush=True)
    if len(merged) < 1000:
        print(f"error: expected thousands of codes, got {len(merged)}", file=sys.stderr)
        return 1
    OUT.parent.mkdir(parents=True, exist_ok=True)
    payload = dict(sorted(merged.items()))
    raw = json.dumps(payload, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
    OUT.write_bytes(gzip.compress(raw, compresslevel=9))
    print(f"wrote {OUT} entries={len(payload)} bytes={OUT.stat().st_size}")
    print(f"sample P0420={payload.get('P0420')}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
