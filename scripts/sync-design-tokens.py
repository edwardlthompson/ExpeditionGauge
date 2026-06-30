#!/usr/bin/env python3
"""Generate platform design outputs from design-tokens/design-tokens.json."""
from __future__ import annotations

import hashlib
import json
import sys
from pathlib import Path

HEADER = "GENERATED — do not edit; run scripts/sync-design-tokens.py"


def repo_root() -> Path:
    return Path(__file__).resolve().parent.parent


def load_tokens(root: Path) -> dict:
    path = root / "design-tokens" / "design-tokens.json"
    return json.loads(path.read_text(encoding="utf-8"))


def token_hash(tokens: dict) -> str:
    raw = json.dumps(tokens, sort_keys=True).encode("utf-8")
    return hashlib.sha256(raw).hexdigest()[:12]


def hex_to_compose(name: str, hex_color: str, *, private: bool = False) -> str:
    h = hex_color.lstrip("#")
    prefix = "private val " if private else "val "
    if len(h) == 8:
        return f"{prefix}{name} = Color(0x{h.upper()})"
    return f"{prefix}{name} = Color(0xFF{h.upper()})"


def camel_case(key: str) -> str:
    parts = key.replace("-", "_").split("_")
    return parts[0] + "".join(p.capitalize() for p in parts[1:])


def color_role_name(key: str) -> str:
    return camel_case(key)


def generate_css(tokens: dict, digest: str) -> str:
    colors = tokens["color"]
    spacing = tokens["spacing"]
    radius = tokens["radius"]
    typo = tokens["typography"]
    meta = tokens["meta"]

    def vars_block(mode: str) -> list[str]:
        lines = []
        for key, value in colors.items():
            css_key = key.replace("on", "on-").replace("Variant", "-variant")
            css_key = "--gp-color-" + _kebab(key)
            lines.append(f"  {css_key}: {value[mode]};")
        for key, value in spacing.items():
            lines.append(f"  --gp-space-{key}: {value}px;")
        for key, value in radius.items():
            lines.append(f"  --gp-radius-{key}: {value}px;")
        lines.append(f"  --gp-font-sans: {typo['fontFamily']['sans']};")
        for scale_key, scale in typo["scale"].items():
            kebab = _kebab(scale_key)
            lines.append(f"  --gp-text-{kebab}-size: {scale['sizeRem']}rem;")
            lines.append(f"  --gp-text-{kebab}-line: {scale['lineHeight']};")
            lines.append(f"  --gp-text-{kebab}-weight: {scale['weight']};")
        return lines

    light_lines = vars_block("light")
    dark_lines = vars_block("dark")

    parts = [
        f"/* {HEADER} */",
        f"/* source-hash: {digest} */",
        "",
        ":root,",
        '[data-theme="light"] {',
        *light_lines,
        "}",
        "",
        '[data-theme="dark"] {',
        *dark_lines,
        "}",
        "",
        '[data-theme="system"] {',
        *light_lines,
        "}",
        "",
        "@media (prefers-color-scheme: dark) {",
        '  [data-theme="system"] {',
        *dark_lines,
        "  }",
        "}",
        "",
    ]
    return "\n".join(parts)


def _kebab(key: str) -> str:
    out: list[str] = []
    for i, ch in enumerate(key):
        if ch.isupper() and i > 0:
            out.append("-")
        out.append(ch.lower())
    return "".join(out)


def _gauge_color_name(key: str) -> str:
    mapping = {
        "background": "GaugeBackground",
        "scaleWhite": "GaugeScaleWhite",
        "green": "GaugeGreen",
        "yellow": "GaugeYellow",
        "red": "GaugeRed",
        "ball": "GaugeBall",
    }
    return mapping.get(key, f"Gauge{camel_case(key)}")


def _playback_color_name(key: str) -> str:
    return f"Playback{key[0].upper()}{key[1:]}"


def generate_color_kt(tokens: dict, digest: str) -> str:
    colors = tokens["color"]
    gauge = tokens.get("gauge", {})
    playback = tokens.get("playback", {})
    lines = [
        f"// {HEADER}",
        f"// source-hash: {digest}",
        "package dev.foss.expeditiongauge.ui.theme",
        "",
        "import androidx.compose.material3.darkColorScheme",
        "import androidx.compose.material3.lightColorScheme",
        "import androidx.compose.ui.graphics.Color",
        "",
        "// Raw palette",
    ]
    for key in colors:
        role = color_role_name(key)
        cap = role[0].upper() + role[1:]
        lines.append(hex_to_compose(f"GpLight{cap}", colors[key]["light"], private=True))
        lines.append(hex_to_compose(f"GpDark{cap}", colors[key]["dark"], private=True))
    lines.extend([
        "",
        "val LightExpeditionGaugeColors = lightColorScheme(",
        *[f"    {color_role_name(k)} = GpLight{color_role_name(k)[0].upper()}{color_role_name(k)[1:]}," for k in colors],
        ")",
        "",
        "val DarkExpeditionGaugeColors = darkColorScheme(",
        *[f"    {color_role_name(k)} = GpDark{color_role_name(k)[0].upper()}{color_role_name(k)[1:]}," for k in colors],
        ")",
        "",
        "// Gauge HUD palette (from design-tokens.json → gauge)",
    ])
    for key, hex_color in gauge.items():
        lines.append(hex_to_compose(_gauge_color_name(key), hex_color))
    if playback:
        lines.extend([
            "",
            "// Playback / map overlay palette (from design-tokens.json → playback)",
        ])
        for key, hex_color in playback.items():
            lines.append(hex_to_compose(_playback_color_name(key), hex_color))
    lines.extend([
        "",
        "// High-contrast accessibility palette (Sprint 17)",
        "val HighContrastExpeditionGaugeColors = darkColorScheme(",
        "    primary = Color(0xFFFFFFFF),",
        "    onPrimary = Color(0xFF000000),",
        "    primaryContainer = Color(0xFF000000),",
        "    onPrimaryContainer = Color(0xFFFFFFFF),",
        "    secondary = Color(0xFFFFFF00),",
        "    onSecondary = Color(0xFF000000),",
        "    secondaryContainer = Color(0xFF000000),",
        "    onSecondaryContainer = Color(0xFFFFFF00),",
        "    tertiary = Color(0xFFFFFFFF),",
        "    onTertiary = Color(0xFF000000),",
        "    error = Color(0xFFFF0000),",
        "    onError = Color(0xFFFFFFFF),",
        "    background = Color(0xFF000000),",
        "    onBackground = Color(0xFFFFFFFF),",
        "    surface = Color(0xFF000000),",
        "    onSurface = Color(0xFFFFFFFF),",
        "    surfaceVariant = Color(0xFF1A1A1A),",
        "    onSurfaceVariant = Color(0xFFFFFFFF),",
        "    outline = Color(0xFFFFFFFF),",
        ")",
        "",
        "// Day brightness: higher contrast for outdoor readability",
        "val DayExpeditionGaugeColors = darkColorScheme(",
        "    primary = GpDarkPrimary,",
        "    onPrimary = GpDarkOnPrimary,",
        "    primaryContainer = GpDarkPrimaryContainer,",
        "    onPrimaryContainer = GpDarkOnPrimaryContainer,",
        "    secondary = Color(0xFFB8D4FF),",
        "    onSecondary = Color(0xFF000000),",
        "    secondaryContainer = GpDarkSecondaryContainer,",
        "    onSecondaryContainer = GpDarkOnSecondaryContainer,",
        "    tertiary = GpDarkTertiary,",
        "    onTertiary = GpDarkOnTertiary,",
        "    error = GpDarkError,",
        "    onError = GpDarkOnError,",
        "    background = Color(0xFF000000),",
        "    onBackground = Color(0xFFFFFFFF),",
        "    surface = Color(0xFF0A0A0A),",
        "    onSurface = Color(0xFFFFFFFF),",
        "    surfaceVariant = GpDarkSurfaceVariant,",
        "    onSurfaceVariant = Color(0xFFE0E0E0),",
        "    outline = Color(0xFFCCCCCC),",
        ")",
        "",
    ])
    return "\n".join(lines)


def _text_style(name: str, scale: dict) -> str:
    lh = scale["sizeSp"] * scale["lineHeight"]
    return (
        f"val {name} = TextStyle(\n"
        f"    fontSize = {scale['sizeSp']}.sp,\n"
        f"    lineHeight = {lh:.1f}.sp,\n"
        f"    fontWeight = FontWeight({scale['weight']}),\n"
        f")"
    )


def generate_type_kt(tokens: dict, digest: str) -> str:
    scale = tokens["typography"]["scale"]
    display = tokens["typography"].get("displayScale", {})
    m3_entries = []
    for key, val in scale.items():
        m3_entries.append(
            f"    {key} = TextStyle(\n"
            f"        fontSize = {val['sizeSp']}.sp,\n"
            f"        lineHeight = {(val['sizeSp'] * val['lineHeight']):.1f}.sp,\n"
            f"        fontWeight = FontWeight({val['weight']}),\n"
            f"    ),"
        )
    display_lines = []
    display_names = {
        "speedLarge": "GaugeSpeedTextStyle",
        "headingLarge": "GaugeHeadingTextStyle",
        "gaugeLabel": "GaugeLabelTextStyle",
    }
    for key, val in display.items():
        display_lines.append(_text_style(display_names.get(key, camel_case(key)), val))
    return "\n".join([
        f"// {HEADER}",
        f"// source-hash: {digest}",
        "package dev.foss.expeditiongauge.ui.theme",
        "",
        "import androidx.compose.material3.Typography",
        "import androidx.compose.ui.text.TextStyle",
        "import androidx.compose.ui.text.font.FontWeight",
        "import androidx.compose.ui.unit.sp",
        "",
        "val ExpeditionGaugeTypography = Typography(",
        *m3_entries,
        ")",
        "",
        "// Gauge display typography (from design-tokens.json → typography.displayScale)",
        *display_lines,
        "",
    ])


def generate_dimens_kt(tokens: dict, digest: str) -> str:
    spacing = tokens["spacing"]
    radius = tokens["radius"]
    elevation = tokens["elevation"]
    lines = [
        f"// {HEADER}",
        f"// source-hash: {digest}",
        "package dev.foss.expeditiongauge.ui.theme",
        "",
        "import androidx.compose.ui.unit.dp",
        "",
    ]
    for key, val in spacing.items():
        name = key[0].upper() + key[1:]
        lines.append(f"val Spacing{name} = {val}.dp")
    lines.append("")
    for key, val in radius.items():
        name = key[0].upper() + key[1:]
        lines.append(f"val Radius{name} = {val}.dp")
    lines.append("")
    for key, val in elevation.items():
        name = key.replace("level", "Level")
        lines.append(f"val Elevation{name} = {val}.dp")
    lines.append("")
    return "\n".join(lines)


def generate_theme_meta(tokens: dict) -> str:
    meta = tokens["meta"]
    payload = {
        "themeColorLight": meta["themeColorLight"],
        "themeColorDark": meta["themeColorDark"],
        "name": meta["name"],
    }
    return json.dumps(payload, indent=2) + "\n"


def write_outputs(root: Path) -> None:
    tokens = load_tokens(root)
    digest = token_hash(tokens)
    synced: list[str] = []

    web_root = root / "examples" / "web"
    if web_root.is_dir():
        web_css = web_root / "src" / "design-tokens.css"
        theme_meta = web_root / "src" / "theme-meta.json"
        web_css.parent.mkdir(parents=True, exist_ok=True)
        web_css.write_text(generate_css(tokens, digest), encoding="utf-8")
        theme_meta.write_text(generate_theme_meta(tokens), encoding="utf-8")
        synced.append("web")

    android_root = root / "examples" / "android"
    if android_root.is_dir():
        android_theme = (
            android_root
            / "app"
            / "src"
            / "main"
            / "java"
            / "dev"
            / "foss"
            / "expeditiongauge"
            / "ui"
            / "theme"
        )
        android_theme.mkdir(parents=True, exist_ok=True)
        (android_theme / "Color.kt").write_text(generate_color_kt(tokens, digest), encoding="utf-8")
        (android_theme / "Type.kt").write_text(generate_type_kt(tokens, digest), encoding="utf-8")
        (android_theme / "Dimens.kt").write_text(generate_dimens_kt(tokens, digest), encoding="utf-8")
        synced.append("android")

    if not synced:
        print("No active example stacks for design token sync; skipped")
        return

    print(f"Synced design tokens for {', '.join(synced)} (hash {digest})")


def main() -> None:
    root = repo_root()
    if not (root / "design-tokens" / "design-tokens.json").is_file():
        print("Missing design-tokens/design-tokens.json", file=sys.stderr)
        sys.exit(1)
    write_outputs(root)


if __name__ == "__main__":
    main()
