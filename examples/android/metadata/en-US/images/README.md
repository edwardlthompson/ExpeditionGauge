# F-Droid / Fastlane images

Store listing assets for ExpeditionGauge v1.1.0.

| File | Size | Source |
|------|------|--------|
| `icon.png` | 512×512 | Canonical source: `docs/assets/app-icon-512.png` via `scripts/expedition/sync-app-icon.py` |
| `featureGraphic.png` | 1024×500 | Generated store banner |
| `phoneScreenshots/01_dashboard_hud.png` | Device landscape | Live HUD (pitch, roll, latG, speed) |
| `phoneScreenshots/02_playback_graphs.png` | Device landscape | Playback telemetry graph panel |
| `phoneScreenshots/03_playback_heatmap.png` | Device landscape | Playback map + heatmap metric chips |
| `phoneScreenshots/04_playback_ghost_lap.png` | Device landscape | Ghost lap compare panel |

Regenerate screenshots on a physical device:

```powershell
pwsh scripts/expedition/capture-fdroid-screenshots.ps1 -Serial b5214fc6
```

Paths are referenced from `metadata/en-US/` (manual F-Droid) or `fastlane/metadata/android/en-US/` (Fastlane).
