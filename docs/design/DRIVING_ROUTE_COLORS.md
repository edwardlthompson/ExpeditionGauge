# Driving route colors

Playback map polylines and session list thumbnails use **longitudinal acceleration** buckets (not drift β by default).

| Bucket | Condition (`lonAccel`) | Color |
|--------|------------------------|-------|
| **Brake** | `< -0.25 g` | `#FF3333` |
| **Accel** | `> +0.25 g` | `#33CC33` |
| **Coast** | otherwise | `#FFCC00` |

Implementation: `DrivingRouteStyling` in `examples/android/.../playback/`.

Thresholds are constants (`BRAKE_THRESHOLD_G`, `ACCEL_THRESHOLD_G`) — tune on device after real drives.

Heatmap β overlay remains optional via playback overlay controls.
