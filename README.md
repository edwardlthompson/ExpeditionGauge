# ExpeditionGauge

![MIT](https://img.shields.io/badge/license-MIT-2ea043?style=flat-square)
![Android](https://img.shields.io/badge/Android-FOSS-3DDC84?style=flat-square)
![Version](https://img.shields.io/badge/version-2.10.1-0969da?style=flat-square)

Offline-first automotive HUD for off-road and track driving — Compose gauges, GPS/IMU fusion, BLE sensors, session recording, and playback with export.

**Package:** `dev.foss.expeditiongauge` · **License:** MIT · **No Google Play Services or Firebase in the APK.**

## Features

| Area | Capabilities |
|------|----------------|
| **Live HUD** | Rotation-aware G-meter with trail, digital speed (imperial/metric), pitch/roll, DMS coords, TPMS 2×2 grid |
| **Dashboard chrome** | Hamburger menu, dark menu surfaces, top-bar Play/Stop recording, Set Level on G-meter |
| **Sensors** | Phone IMU/GPS, BLE IMU, TPMS, external NMEA GPS, OBD-II (Classic Bluetooth) |
| **Recording** | Room sessions, dashcam loop storage cap, protect session, BT auto-record triggers |
| **Playback** | MapLibre route map, scrubber, elevation profile, ghost lap, media markers |
| **Export & share** | GPX/ZIP, playback video burn-in, 3D flyover MP4, stats card share sheet |
| **Live telemetry** | Opt-in P2P sender/receiver (WebSocket signaling) |
| **Android Auto** | Live metrics via AndroidX Car App Library (user-installed host app) |

Shipped through **v2.10.1** — HUD readability (digital speed, imperial units, MSL altitude, dark menus, TPMS grid). Prior **v2.10.0** added G-trail, rotation axes, drawer chrome, storage loop, auto-record. See [`CHANGELOG.md`](CHANGELOG.md) and [`docs/features/dashboard-hud-v2.md`](docs/features/dashboard-hud-v2.md).

## Quick start

### Build debug APK

```bash
export SOURCE_DATE_EPOCH=1700000000
cd examples/android
./gradlew assembleDebug
```

Install the APK from `examples/android/app/build/outputs/apk/debug/`. Requires Android 8+ (API 26).

### Run unit tests

```bash
bash scripts/sync-app-update-from-config.sh
cd examples/android
./gradlew :app:testDebugUnitTest
```

### Device smoke tests

Connect hardware via USB ADB, then:

```powershell
pwsh scripts/expedition/adb-smoke.ps1 -Scenario cold-start
```

Recording uses top-bar `record_play` / `record_stop` icons (not bottom buttons). Scenario list: `scripts/expedition/adb-smoke.ps1`.

## Privacy & security

- **Local-first** — session data stays on device unless you export or enable live telemetry
- **Loop recording** — oldest unprotected sessions auto-deleted when storage cap is reached; protect drives in session metadata
- **Auto-record** — optional start/stop when a bonded Bluetooth trigger device connects/disconnects (local ACL only)
- **`allowBackup=false`** — sessions are not included in Android cloud backup
- **Opt-in network** — update checks and live telemetry are off by default

See [`docs/PRIVACY.md`](docs/PRIVACY.md), [`SECURITY.md`](SECURITY.md), and [`docs/THREAT_MODEL.md`](docs/THREAT_MODEL.md).

## Agent development

This repo uses [agent-project-bootstrap](https://github.com/edwardlthompson/agent-project-bootstrap) scaffolding for Cursor agents.

| Doc | Purpose |
|-----|---------|
| [`docs/START_HERE.md`](docs/START_HERE.md) | Agent cold-start read order |
| [`BUILD_PLAN.md`](BUILD_PLAN.md) | Active sprint board |
| [`docs/help/BATCH_COMMANDS.md`](docs/help/BATCH_COMMANDS.md) | Slash commands (`/build`, `/verify`, `/ship`) |
| [`AGENTS.md`](AGENTS.md) | Router and session protocol |

Resume the next task: `pwsh scripts/expedition/resume-agent.ps1`

## Repository layout

```
examples/android/   ExpeditionGauge app (Compose, Room, MapLibre)
docs/               Agent docs, design specs, ADRs, feature specs
scripts/            Gates, CI helpers, ADB smokes
```

## License

MIT — see [`LICENSE`](LICENSE).
