# AA + phone inclinometer visual contract

Reference photo: [`gauge-reference/aa-inclinometer-reference.png`](gauge-reference/aa-inclinometer-reference.png)

Simplified pitch/roll inclinometer for **Android Auto** (bitmap `CarIcon`) and phone **`AttitudeGaugeMode.INCLINOMETER`** / Offroad preset. Ball-in-ring G-meter (`GAUGE_REFERENCE.md`) is unchanged for other gauge modes.

## Scale

| Constant | Value |
|----------|--------|
| `INCLINOMETER_MAX_DEG` | **45°** full scale (AA + inclinometer mode only) |
| Tick marks | 15° · 30° · 45° |
| Pitch bars | 5 per side of center (9° per step) |
| Roll segments | 7 per side arc (≈6.4° per step) |

Display clamps segment lighting at ±45°; numeric readout shows true angle beyond scale.

## Sign convention

Matches phone `AttitudeBallLogic` / `GAUGE_REFERENCE.md`:

- **Negative pitch** (nose down / braking): lights **upper** center bars
- **Positive pitch** (nose up): lights **lower** center bars
- **Negative roll**: lights **left** arc
- **Positive roll**: lights **right** arc

No `GaugeDisplayRotation` remap on inclinometer — vehicle-forward axes for crawling and camper leveling.

## Color

Progressive **green → yellow → red** per segment using the same normalized lerp as `GmeterBallColor` (`|segmentAngle| / 45°`).

## Angle alerts

Reuse Settings **Max pitch (°)** / **Max roll (°)** with Alerts master on. Threshold markers drawn on gauge when set (clamped to 45°). Active alert: red border on phone; red frame on AA bitmap.

## Preset behavior

Selecting **Offroad** on the dashboard sets `AttitudeGaugeMode.INCLINOMETER`. Switching to another preset (Drift, Track, etc.) does **not** auto-revert gauge mode — change it under Settings → Display → Attitude gauge if needed.

## AA asset

- Square bitmap targeting **128×128 dp** (`GridItem.IMAGE_TYPE_LARGE`)
- Text under image: `P ±xx.x°` · `R ±xx.x°` only (no G-force)
