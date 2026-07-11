#!/usr/bin/env python3
"""Find center tap coords for a uiautomator text/content-desc label."""
from __future__ import annotations

import re
import sys
from pathlib import Path


def main() -> int:
    path = Path(sys.argv[1])
    label = sys.argv[2]
    xml = path.read_text(encoding="utf-8", errors="replace")
    esc = re.escape(label)
    m = re.search(
        rf'(?:text|content-desc)="{esc}"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"',
        xml,
        re.I,
    )
    if not m:
        m = re.search(
            rf'(?:text|content-desc)="[^"]*{esc}[^"]*"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"',
            xml,
            re.I,
        )
    if not m:
        print("NOT_FOUND", file=sys.stderr)
        return 1
    x1, y1, x2, y2 = map(int, m.groups())
    print(f"{(x1 + x2) // 2} {(y1 + y2) // 2}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
