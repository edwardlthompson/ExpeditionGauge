#!/usr/bin/env python3
"""Build slim CC0 OBDex DTC catalog (code -> English title) for app assets.

Source: https://github.com/foerbsnavi/OBDex (LICENSE-DATA: CC0-1.0)
Pages CDN may 404; we parse data/generic/*_enriched.yaml from GitHub raw.

A single JSON blob is ~546 KB and trips the 500 KB hygiene gate. Gzip hid
that from git but F-Droid's scanner treats .gz as an unknown binary, so we
write multiple JSON shards instead. Regen:

  python3 scripts/expedition/fetch-obdex-dtc.py
"""
from __future__ import annotations

import json
import re
import sys
import urllib.request
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[2]
OUT_DIR = REPO_ROOT / "examples/android/app/src/main/assets/dtc"
# Stay under scripts/check-large-tracked-files.sh (500 KB).
MAX_SHARD = 500 * 1024 - 8192
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


def dump_shard(payload: dict[str, str]) -> bytes:
    return json.dumps(payload, ensure_ascii=False, separators=(",", ":")).encode("utf-8")


def write_shards(payload: dict[str, str]) -> list[Path]:
    OUT_DIR.mkdir(parents=True, exist_ok=True)
    for old in OUT_DIR.glob("obdex_en*"):
        if old.suffix in {".gz", ".json"} or old.name.endswith(".json.gz"):
            old.unlink()
    shards: list[dict[str, str]] = []
    current: dict[str, str] = {}
    for code, title in payload.items():
        trial = dict(current)
        trial[code] = title
        if current and len(dump_shard(trial)) > MAX_SHARD:
            shards.append(current)
            current = {code: title}
        else:
            current = trial
    if current:
        shards.append(current)
    written: list[Path] = []
    for i, shard in enumerate(shards, 1):
        path = OUT_DIR / f"obdex_en_{i}.json"
        data = dump_shard(shard)
        if len(data) > MAX_SHARD:
            print(f"error: shard {path.name} is {len(data)} bytes", file=sys.stderr)
            sys.exit(1)
        path.write_bytes(data)
        written.append(path)
        print(f"wrote {path} entries={len(shard)} bytes={len(data)}")
    return written


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
    payload = dict(sorted(merged.items()))
    write_shards(payload)
    print(f"sample P0420={payload.get('P0420')}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
