#!/usr/bin/env python3
"""Parse uiautomator dump XML; print texts; exit 0 if ExpeditionGauge listed."""
from __future__ import annotations

import re
import sys
from pathlib import Path


def main() -> int:
    path = Path(sys.argv[1] if len(sys.argv) > 1 else ".cursor/aa-ui-0.xml")
    xml = path.read_text(encoding="utf-8", errors="replace")
    texts = re.findall(r'(?:text|content-desc)="([^"]+)"', xml)
    for t in texts:
        if t.strip():
            print(t)
    listed = bool(
        re.search(r"ExpeditionGauge|dev\.foss\.expeditiongauge", xml, re.I)
    )
    print(f"LISTED={listed}")
    return 0 if listed else 1


if __name__ == "__main__":
    raise SystemExit(main())
