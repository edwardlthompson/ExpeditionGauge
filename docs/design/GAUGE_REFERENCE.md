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
- Crosshairs; animated ball from calibrated pitch/roll or lat/lon G
- **Display-rotation-aware axes:** `GaugeDisplayRotation` maps device-frame G to screen coordinates so screen Y = longitudinal (forward/back) and screen X = lateral at all four `Surface.ROTATION_*` values
- **G-ball trail:** fading dot trail (~40 samples) while **recording** in all modes; dedupe &lt; 0.02; cleared on calibrate or session stop
- On-cube: canvas + **edge numerals** (roll ° left/right; lon G top/bottom in G-force modes)
- Tap cube → detail sheet with pitch, roll, lat/lon G, peaks, **Calibrate / Set Level**

## Center — Speed / GPS / HDG

- Large white speed digits (digital only — no arc gauge); yellow unit label respects imperial/metric setting
- Numeric heading only (`247°` + yellow `HDG`) — **no compass dial**
- DMS coordinates (two lines)
- Time top-right; trip/odo bottom
- Clock and mountain icons for time and altitude in telemetry cube

## Right — Tire pressures

- Top-down tire icons; FL/RL start-aligned, FR/RR end-aligned
- Center low-pressure icon (blink unless reduced motion / high contrast)
- `TpmsPressureBands`: LOW &lt; 28 PSI, CRITICAL &lt; 25 PSI (stored kPa)
- Yellow status icons (GPS fix, etc.)
- Voltage when OBD available

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
