# GAUGE_REFERENCE — ExpeditionGauge HUD Visual Contract

Reference photo: [`gauge-reference/hud-reference.png`](gauge-reference/hud-reference.png)

Sprint 2 acceptance criteria **must** match this document. Intentional deviations from the reference hardware photo are documented below.

## Layout

Rotation-aware **cube tiles** (`HudSquareTile`) on `#000000` background:

| Mode | Arrangement |
|------|-------------|
| Portrait `THREE_TILE` | Column: G-meter · telemetry · TPMS (height ≥ 480dp) |
| Landscape `THREE_TILE` | Row: G-meter · telemetry · TPMS (width ≥ 360dp) |
| `TWO_TILE` fallback | G-meter + combined telemetry/TPMS cube |

Legacy reference: landscape three-panel `Row` maps to cube row at sufficient width.

| Left / tile 1 | Center / tile 2 | Right / tile 3 |
|------|--------|-------|
| Attitude G-meter (ball-in-ring) | Speed + numeric HDG + GPS coords | Tire pressures FL/FR/RL/RR |

## Palette (design tokens)

| Token | Use |
|-------|-----|
| `gaugeBackground` | `#000000` |
| `gaugeScaleWhite` | Rings, primary digits |
| `gaugeGreen` | Safe zones |
| `gaugeYellow` | Unit labels, HDG label, status icons |
| `gaugeRed` | Warning segments, record indicator |

## Left — Attitude G-meter

- Concentric rings at 10° / 20° / 30° reference (attitude) and 0.5G / 1.0G / 1.5G (G-force / hybrid)
- **Thick rings and crosshairs** (4px+ stroke) for in-car distance readability
- **Display-rotation-aware axes:** `GaugeDisplayRotation` — full pipeline in [`GMETER_HUD_ROTATION.md`](GMETER_HUD_ROTATION.md)
- **G-ball trail:** colored line + dots (~40 samples) while **recording**; green→yellow→red by distance from center; dedupe &lt; 0.015; cleared on calibrate or session stop
- Ball fill **green → yellow → red** as deflection increases; dark outline for contrast
- On-cube: canvas + **directional edge numerals** — pitch ° on top/bottom only when ball is there (`--` on idle side); roll ° on left/right only on active side
- Braking (negative pitch) moves ball toward **top**; acceleration toward **bottom**
- Portrait HUD cube: extra **90° clockwise** ball rotation; roll ° on top/bottom edges, pitch ° on left/right
- Tap cube → detail sheet with pitch, roll, lat/lon G, peaks, **Calibrate / Set Level**

## Center — Speed / GPS / HDG

- Enlarged row: **000** speed beside **000** heading (40sp base, zero-padded)
- Meta row: altitude + clock time; GPS source · sats · HDOP below
- Bold monospace DMS coordinates (two lines); pitch/roll and OBD extras when available
- Split top chrome: menu (start) and record (end) overlay the HUD — no full-width app bar
- Dashboard content respects **navigation bar** inset; nothing draws behind system nav buttons
- Yellow unit labels respect imperial/metric setting

## Right — Tire pressures

- Top-down **sedan schematic** centered (tapered hood, cabin glass, four wheels)
- Each corner: label chip (dark mode: **black / white text**), bold pressure, temperature, **battery icon**
- Battery fill from BLE `batteryPct` when sensors connected (see [`TPMS_LAYOUT.md`](TPMS_LAYOUT.md))
- Center low-pressure icon (blink unless reduced motion / high contrast)
- High-contrast white-on-black regardless of app light/dark theme

## Deviations from reference photo

| Zone | Reference | ExpeditionGauge |
|------|-----------|-----------------|
| Left | Vertical inclinometer bars | Ball-in-ring attitude G-meter |
| Right | Compass dial | Tire pressure panel |
| Center | Speed + coords | Speed + coords + numeric HDG |

## Calibration

`CalibrationStore` zeros pitch/roll at level surface. Document offsets in session metadata when recording.

## Dashboard chrome (v2.10+)

- **Top bar:** hamburger menu, Play/Stop record icon (`record_play` / `record_stop`), mark-event icon when recording
- **Drawer:** dark `GaugeMenuSurface`; sessions, stats, presets, IMU, live, settings, about, theme, recording options
- **Sub-screens:** settings/sessions/about use `GaugeBackHandler`; drawer back closes before dashboard exit
- **Portrait HUD:** full DMS + MSL altitude, prominent digital speed, TPMS 2×2 grid with `--` placeholders when no sensors
