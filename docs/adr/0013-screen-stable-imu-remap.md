# ADR-0013: Screen-stable IMU remap for inclinometer

- **Status:** Accepted
- **Date:** 2026-07-09
- **Deciders:** ExpeditionGauge (validated on OnePlus 12 `b5214fc6`)

## Context

Portrait inclinometer / aviation horizon was correct. After Zero in portrait and a
90° CCW phone rotation (`Surface.ROTATION_90`), the horizon stayed vertical
(sky left / ground right) as if the phone had not rotated with the UI.

Two bugs stacked:

1. **Stale rotation** — Application `WindowManager.defaultDisplay.rotation` on
   some OEMs reports `ROTATION_0` while the Activity display is already
   `ROTATION_90`. Re-reading that on every gyro sample wiped the correct value
   from `view.display.rotation`.
2. **Wrong correction layer** — Post-Madgwick Euler “unwrap” (`pitch±90` /
   `roll±180`) is fragile at gimbal singularities. The same physical bank can
   appear as either form; fighting Euler angles after fusion cannot stay stable.

## Decision

1. **Remap IMU axes before Madgwick** via `SensorAxisRemap` so accel/gyro are in
   a **screen-stable** frame (+Y up the current UI, +X right) — equivalent to
   portrait device axes at `ROTATION_0`.
2. **Locked portrait pitch↔roll swap** in `VehicleAttitudeLogic` after fusion
   (unchanged; portrait visual contract).
3. **Activity `Display.getRotation()` is authoritative** when the HUD is visible
   (`DashboardScreen` → `updateDisplayRotation`). Do **not** overwrite that from
   Application `WindowManager` on the gyro path.
4. **Fallback reader** uses `DisplayManager.getDisplay(DEFAULT_DISPLAY).rotation`,
   not Application WM.
5. **Reset Madgwick** when `displayRotation` changes so the quaternion re-aligns
   to remapped gravity.
6. **One Zero** stores vehicle-frame offsets; works in every orientation after
   the remap.

## Consequences

- Landscape horizon stays level after a portrait Zero (sky top / ground bottom).
- Do **not** reintroduce post-fusion Euler unwrap as the primary landscape fix.
- Do **not** call Application WM rotation from `PhoneSensorProvider.onSensorChanged`.
- G-meter still uses device-frame accel + `GaugeDisplayRotation` ball remaps
  (separate pipeline; do not double-correct attitude fusion with ball remaps).
- Contract + tests: `docs/design/GMETER_HUD_ROTATION.md`,
  `SensorAxisRemapTest`, `VehicleAttitudeLogicTest`.

## Alternatives Considered

| Alternative | Rejected because |
|-------------|------------------|
| Euler unwrap after Madgwick | Gimbal singularity; oscillates between roll±180 and pitch±90 |
| Re-zero per orientation | User requires one Zero across portrait/landscape |
| Application WindowManager on every sample | OEM reports ROTATION_0 while Activity is ROTATION_90 |
| Rotate only the Canvas horizon graphic | Numbers/labels would still be wrong; fusion must be screen-stable |

## Related

- [`docs/design/GMETER_HUD_ROTATION.md`](../design/GMETER_HUD_ROTATION.md)
- [`docs/design/AA_INCLINOMETER.md`](../design/AA_INCLINOMETER.md)
- ADR-0003 (sensor architecture), ADR-0009 (dual orientation)
