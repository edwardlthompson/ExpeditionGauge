# GAUGE_REFERENCE — ExpeditionGauge HUD Visual Contract

Reference photo: [`gauge-reference/hud-reference.png`](gauge-reference/hud-reference.png)

Sprint 2 acceptance criteria **must** match this document. Intentional deviations from the reference hardware photo are documented below.

## Layout

Landscape three-panel `Row` on `#000000` background:

| Left | Center | Right |
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
- **G-ball trail:** fading dot trail (~40 samples) in G-force and hybrid modes; cleared on calibrate or session stop
- Digital readouts: `Pitch: ±XX.X°`, `Roll: ±XX.X°`; lat/lon G as **whole numbers** (`Lat G: N`, `Lon G: N`)
- Color zones: green / yellow / red by threshold
- **Calibrate / Set Level** — sole full `Button` on the main HUD column

## Center — Speed / GPS / HDG

- Large white speed digits; yellow unit label
- Numeric heading only (`247°` + yellow `HDG`) — **no compass dial**
- DMS coordinates (two lines)
- Time top-right; trip/odo bottom
- Broken white arc border

## Right — Tire pressures

- FL / FR / RL / RR with large values; `--` when no TPMS
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
- **Drawer:** sessions, stats, presets, IMU, live, settings, about, theme, recording options
- **Portrait HUD:** full DMS + altitude, prominent speed, TPMS grid with `--` placeholders when no sensors
