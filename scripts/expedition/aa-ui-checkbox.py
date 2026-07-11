#!/usr/bin/env python3
"""Return tap coords for the checkbox near an app label in Customize launcher dump."""
from __future__ import annotations

import re
import sys
from pathlib import Path

xml = Path(sys.argv[1]).read_text(encoding="utf-8", errors="replace")
label = sys.argv[2]
# Prefer checked/checkable node near the label row bounds.
m = re.search(
    rf'text="{re.escape(label)}"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"',
    xml,
)
if not m:
    print("NOT_FOUND", file=sys.stderr)
    raise SystemExit(1)
x1, y1, x2, y2 = map(int, m.groups())
cy = (y1 + y2) // 2
# Checkboxes are typically on the right of the row on AA customize list.
# Scan for checkable nodes with overlapping Y.
best = None
for cm in re.finditer(
    r'checkable="true"[^>]*checked="([^"]+)"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"',
    xml,
):
    checked, a, b, c, d = cm.groups()
    a, b, c, d = map(int, (a, b, c, d))
    if b <= cy <= d or y1 <= ((b + d) // 2) <= y2:
        best = ((a + c) // 2, (b + d) // 2, checked)
        break
if not best:
    # fallback: tap far right of row
    print(f"{x2 + 80} {cy} unknown")
else:
    print(f"{best[0]} {best[1]} {best[2]}")
