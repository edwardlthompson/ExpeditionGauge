# AA + phone inclinometer visual contract

Reference photo: [`gauge-reference/aa-inclinometer-reference.png`](gauge-reference/aa-inclinometer-reference.png)

Simplified pitch/roll inclinometer for **Android Auto** (bitmap `CarIcon`) and phone **`AttitudeGaugeMode.INCLINOMETER`** / Offroad preset. Ball-in-ring G-meter (`GAUGE_REFERENCE.md`) is unchanged for other gauge modes.

## Layout (reference-inspired)

| Region | Behavior |
|--------|----------|
| **Center** | Vertical pitch ladder (±45°) with dual rails, tick labels, red pointer triangles, digital pitch readout on top |
| **Left / right** | Roll columns as communicating vessels: one side **fills** as the other **drains**; red pointer tracks roll; digital roll readout at bottom |

## Scale

| Constant | Value |
|----------|--------|
| `INCLINOMETER_MAX_DEG` | **45°** full scale |
| Tick marks | 15° · 30° · 45° |
| Pitch bars | 5 per side of center (9° per step) |
| Roll segments | 10 per side (fill fraction = `(1 ± roll/45) / 2`) |

Display clamps lighting at ±45°; numeric readout shows true angle within scale.

## Sign convention

Visual layout (what the driver sees on the **bitmap**, after screen-axis mapping):

- **Nose down / up** → center pitch ladder (upper / lower bars)
- **Tilt left / right** → side columns fill / drain as communicating vessels

**Phone (LOCKED — ADR-0013):** fusion remaps IMU axes to a screen-stable frame
(`SensorAxisRemap`) **before** Madgwick, then applies the locked portrait
pitch↔roll swap (`VehicleAttitudeLogic`). One Zero works in every orientation.
Do not reintroduce Application-WM rotation overwrites or post-fusion Euler
unwrap. Inclinometer / horizon are a passthrough of fusion. Tap/swipe cycles
`InclinometerStyle`. Long-press opens calibrate; sheet can switch back to G-meter.

**Styles**

| Style | Cue |
|-------|-----|
| `LADDER` | Center pitch bars + L/R roll vessels (offroad dash) |
| `HORIZON` | Artificial horizon (aviation attitude indicator) |
| `DUAL_DIAL` | Twin circular dials P / R |
| `BUBBLE` | Spirit-level tubes (vertical pitch, horizontal roll) |

**Calibration:** `CalibrationStore.zeroToCurrentDisplay` updates shared fusion offsets used by
both G-meter and inclinometer; peak-hold clears when offsets change.

## Angle alerts

Reuse Settings **Max pitch (°)** / **Max roll (°)** with Alerts master on. Threshold markers drawn on pitch rails when set (clamped to 45°). Active alert: red border on phone; red frame on AA bitmap.

## Preset behavior

Selecting **Offroad** on the dashboard sets `AttitudeGaugeMode.INCLINOMETER`. Tap/swipe the gauge to toggle G-meter ↔ inclinometer. Switching presets does **not** auto-revert gauge mode.

## AA asset

- Square bitmap targeting **128×128 dp** (`GridItem.IMAGE_TYPE_LARGE`)
- Bitmap includes pitch/roll digital readouts; AA tile text may still show `P` / `R` lines
