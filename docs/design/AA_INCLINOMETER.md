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

- Bitmap size follows **head-unit** `AaDisplaySpec` (148 dp portrait / 180 dp landscape × car density), not a fixed 256 px
- `GridItem.IMAGE_TYPE_LARGE`; includes pitch/roll digital readouts; AA tile text may still show `P` / `R` lines
- Telemetry / TPMS tiles always carry a `CarIcon` (resource or `CarIcon.APP_ICON`) — Car App Library forbids GridItems with neither image nor loading

## Mixed orientations (phone vs HU)

Phone `Display.rotation` and HU `Configuration` are **independent**:

| Domain | Owns |
|--------|------|
| Phone | IMU `SensorAxisRemap` + Compose HUD layout |
| Head unit | `AaDisplaySpec` (bitmap size, grid tile budget, night mode) |
| Shared | Vehicle-frame `pitchDeg` / `rollDeg` from fusion (ADR-0013) |

Phone portrait + landscape HU (and the reverse) must show the same vehicle P/R. Do not feed phone rotation into AA bitmap layout or AA Zero (`displayRotation = 0`).
