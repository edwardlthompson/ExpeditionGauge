# TPMS HUD layout and sensor battery

Reference layout: top-down vehicle schematic with four corner readouts (see product reference image in repo assets).

## HUD corner block (each tire)

1. **Label chip** — white high-contrast pill (`FL tire ›`) readable on `#000000` gauge background
2. **Pressure** — bold whole-number value + unit (`36 Psi`)
3. **Temperature** — whole degrees (`93°F`)
4. **Battery icon** — horizontal cell; fill level from BLE payload when connected, empty outline when absent

## BLE battery pipeline (implemented)

| Stage | Field | Notes |
|-------|--------|--------|
| Parser | `TpmsReading.batteryVolts` | BR / Pecham parsers decode mfg data |
| `BleTpmsManager` | `TpmsCornerReading.batteryPct` | `(batteryVolts / 3.0f * 100).coerceIn(0, 100)` |
| `TelemetryOrchestrator` | `TirePressureReading.batteryPct` | Copied on each TPMS snapshot merge |
| HUD | `TpmsBatteryIcon` | Green/white fill; yellow ≤35%; red ≤15% |

## Planned follow-ups (when hardware on bench)

- 🔲 `[AGENT]` Low-battery alert threshold in `AlertEngine` (e.g. &lt; 20% for 60s)
- 🔲 `[AGENT]` Show raw `batteryVolts` on TPMS management screen for calibration
- 🔲 `[ADB]` Validate battery icon against four live sensors on OnePlus 12
- 🔲 `[AGENT]` Persist per-sensor battery in session export extras (already has `batteryPct` key in writer)

## Theme / contrast

TPMS HUD uses **fixed high-contrast tokens** on the black dashboard cube (`GaugeScaleWhite`, white label chips with black text). Independent of app light/dark theme so in-car readability matches the reference mockup.
