# G-meter HUD rotation contract

**Do not change without updating unit tests and this doc.**

Reference: [`GAUGE_REFERENCE.md`](GAUGE_REFERENCE.md) · implementation: `gauge/GaugeDisplayRotation.kt`

Validated on OnePlus 12 (`b5214fc6`) in **portrait layout** at `ROTATION_0`.

## Attitude ball space (device)

`AttitudeBallLogic.mapPitchRoll`:

| Component | Ball field |
|-----------|------------|
| Roll | `normalizedX` |
| Pitch | `normalizedY` |

## Pipeline (`mapDeviceBallToHudScreen`)

```
device ball
  → [portrait layout only] mirror pitch (Y) + 90° CW
  → rotateBall(displayRotation)
  → [landscape layout only] applyLandscapePostRemap(displayRotation)
  → HUD screen ball
```

## Portrait HUD tile (`isPortraitLayout == true`)

Locked step before `rotateBall`:

1. Negate pitch (`normalizedY *= −1`)
2. `rotate90Clockwise()` — **never CCW, never skip**

| `displayRotation` | Pitch on screen | Roll on screen | Braking (ROTATION_0) |
|-------------------|-----------------|----------------|----------------------|
| 0 | X (lateral) | Y (vertical) | left |
| 180 | X | Y | mirrored with phone |
| 90 / 270 | follows `rotateBall` | follows `rotateBall` | follows phone |

Edge numerals: **roll** top/bottom (`ball.normalizedY`), **pitch** left/right (`ball.normalizedX`).

**Mistake to avoid:** negating `normalizedX` when you mean pitch — after 90° CW, pitch is on screen X and comes from device Y.

## Landscape HUD tile (`isPortraitLayout == false`)

Target: **pitch on screen Y** (braking → top), **roll on screen X** (positive roll → right).

Post-remap after `rotateBall`:

| `displayRotation` | Post-remap | Why |
|-------------------|------------|-----|
| 0 | none | natural landscape |
| 90 (CCW from portrait) | 90° **CCW** | undoes lateral tilt so pitch stays vertical on tile |
| 180 | negate X and Y | upside-down landscape |
| 270 (CW from portrait) | 90° **CW** | mirror of 90 |

Phone CCW → landscape (`ROTATION_90`) applies **CCW on the ball after display rotation** so the cube keeps pitch up/down and roll left/right. This is the landscape complement of the portrait **CW** cube step.

## Edge numerals

| Layout | Pitch edges | Roll edges |
|--------|-------------|------------|
| Portrait | left / right | top / bottom |
| Landscape | top / bottom | left / right |

Numerals follow `ball.normalizedX` / `ball.normalizedY` after the full pipeline.

## Tests

- `GaugeDisplayRotationPortraitTest` — locked portrait @ `ROTATION_0`
- `GaugeDisplayRotationLandscapeTest` — landscape 90 / 270
- `GaugeDisplayRotationAllOrientationsTest` — portrait 180, landscape 0 / 180
