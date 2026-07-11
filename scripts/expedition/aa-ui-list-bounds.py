#!/usr/bin/env python3
"""Print all text= nodes with bounds from a uiautomator dump."""
from __future__ import annotations

import re
import sys
from pathlib import Path

xml = Path(sys.argv[1]).read_text(encoding="utf-8", errors="replace")
for m in re.finditer(
    r'text="([^"]*)"[^>]*bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', xml
):
    t = m.group(1)
    if t.strip():
        print(f"{t}\t{m.group(2)},{m.group(3)}-{m.group(4)},{m.group(5)}")
