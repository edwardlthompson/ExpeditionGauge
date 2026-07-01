#!/usr/bin/env python3
"""Generate launcher mipmaps and store icons from docs/assets/app-icon-512.png."""
from __future__ import annotations

import sys
from pathlib import Path

try:
    from PIL import Image
except ImportError:
    print("sync-app-icon: install Pillow (pip install Pillow)", file=sys.stderr)
    sys.exit(1)

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "docs" / "assets" / "app-icon-512.png"
RES = ROOT / "examples" / "android" / "app" / "src" / "main" / "res"
STORE_DIRS = [
    ROOT / "examples" / "android" / "metadata" / "en-US" / "images",
    ROOT / "examples" / "android" / "fastlane" / "metadata" / "android" / "en-US" / "images",
]

LEGACY_DP = 48
FOREGROUND_DP = 108
SAFE_FRACTION = 0.72

DENSITIES: dict[str, float] = {
    "mipmap-mdpi": 1.0,
    "mipmap-hdpi": 1.5,
    "mipmap-xhdpi": 2.0,
    "mipmap-xxhdpi": 3.0,
    "mipmap-xxxhdpi": 4.0,
}


def fit_center(src: Image.Image, size: int, safe_fraction: float = SAFE_FRACTION, *, opaque_black: bool = False) -> Image.Image:
    if opaque_black:
        canvas = Image.new("RGBA", (size, size), (0, 0, 0, 255))
    else:
        canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    inner = max(1, int(size * safe_fraction))
    scaled = src.copy()
    scaled.thumbnail((inner, inner), Image.Resampling.LANCZOS)
    offset = ((size - scaled.width) // 2, (size - scaled.height) // 2)
    canvas.paste(scaled, offset, scaled if scaled.mode == "RGBA" else None)
    return canvas


def save_png(img: Image.Image, path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    if img.mode != "RGBA":
        img = img.convert("RGBA")
    img.save(path, format="PNG", optimize=True)


def write_adaptive_xml(path: Path) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
</adaptive-icon>
""",
        encoding="utf-8",
    )


def main() -> int:
    if not SOURCE.is_file():
        print(f"sync-app-icon: missing source {SOURCE}", file=sys.stderr)
        return 1

    src = Image.open(SOURCE).convert("RGBA")

    for folder, scale in DENSITIES.items():
        legacy_px = max(1, round(LEGACY_DP * scale))
        fg_px = max(1, round(FOREGROUND_DP * scale))
        base = RES / folder
        save_png(fit_center(src, legacy_px, safe_fraction=0.88, opaque_black=True), base / "ic_launcher.png")
        save_png(fit_center(src, legacy_px, safe_fraction=0.88, opaque_black=True), base / "ic_launcher_round.png")
        save_png(fit_center(src, fg_px), base / "ic_launcher_foreground.png")

    write_adaptive_xml(RES / "mipmap-anydpi-v26" / "ic_launcher.xml")
    write_adaptive_xml(RES / "mipmap-anydpi-v26" / "ic_launcher_round.xml")

    store_icon = fit_center(src, 512, safe_fraction=0.82)
    rgb_store = Image.new("RGB", store_icon.size, (0, 0, 0))
    rgb_store.paste(store_icon, mask=store_icon.split()[3])
    for store_dir in STORE_DIRS:
        save_png(rgb_store.convert("RGBA"), store_dir / "icon.png")

    print(f"sync-app-icon: OK from {SOURCE.name}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
