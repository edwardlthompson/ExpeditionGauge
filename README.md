# ExpeditionGauge

<p align="center">
  <img src="docs/assets/app-icon-512.png" alt="ExpeditionGauge app icon" width="128" height="128" />
</p>

![MIT](https://img.shields.io/badge/license-MIT-2ea043?style=flat-square)
![Android](https://img.shields.io/badge/Android-FOSS-3DDC84?style=flat-square)
![Version](https://img.shields.io/badge/version-2.14.1-0969da?style=flat-square)

Offline-first automotive HUD for off-road and track driving — Compose gauges, GPS/IMU fusion, BLE sensors, session recording, and playback with export.

**Package:** `dev.foss.expeditiongauge` · **License:** MIT · **No Google Play Services or Firebase in the APK.**

## Features

| Area | Capabilities |
|------|----------------|
| **Live HUD** | Cube layout (G-meter, telemetry, TPMS tiles); rotation-aware axes; digital speed; trail while recording |
| **Dashboard chrome** | Hamburger menu, dark menu surfaces, top-bar Play/Stop recording, Set Level on G-meter |
| **Sensors** | Phone IMU/GPS, BLE IMU, TPMS, external NMEA GPS, OBD-II (Classic Bluetooth) |
| **Recording** | Room sessions, dashcam loop storage cap, protect session, BT auto-record triggers |
| **Playback** | MapLibre route map, scrubber, elevation profile, ghost lap, media markers |
| **Export & share** | GPX/ZIP, playback video burn-in, 3D flyover MP4, stats card share sheet |
| **Live telemetry** | Opt-in P2P sender/receiver (WebSocket signaling) |
| **Android Auto** | 3-tile grid HUD (Attitude / telemetry / TPMS); always-on when host connects — see [`docs/help/ANDROID_AUTO.md`](docs/help/ANDROID_AUTO.md) |

Shipped through **v2.14.1** — Android Auto head-unit discovery (`category.POI`), landscape inclinometer, gauge styles. See [`CHANGELOG.md`](CHANGELOG.md).

## Install without the Play Store (Android Auto)

ExpeditionGauge is FOSS and is **not** on the Play Store. Install the APK from [GitHub Releases](https://github.com/edwardlthompson/ExpeditionGauge/releases), then allow Android Auto to show sideloaded apps:

### 1. Install the phone app

1. On your phone, download the latest **`.apk`** from [Releases](https://github.com/edwardlthompson/ExpeditionGauge/releases).
2. Open the file and tap **Install**. If Android asks, allow installs from your browser or file manager.

### 2. Turn on Android Auto developer mode

1. Open the **Android Auto** app on your phone (search Settings or your app drawer).
2. Tap the **menu** (⋮ or ☰) → **About** / **Version**.
3. Tap the **version number about 10 times** until you see a message that developer mode is unlocked.
4. Go back. Open the menu again → **Developer settings** (it only appears after step 3).

### 3. Allow unknown sources

1. In **Developer settings**, turn on **Unknown sources**.
2. If Android asks which apps may start Android Auto, allow **ExpeditionGauge** (or “All apps” / your installer, depending on the phone).

### 4. Enable in Customize launcher

1. Open **Android Auto** settings (not Developer settings).
2. Tap **Customize launcher** (sometimes under Apps / Launcher).
3. Find **ExpeditionGauge** and turn it **on** (checked).

### 5. Use it in the car

1. Plug in USB (preferred for first connect; or pair wireless Android Auto if your car supports it).
2. On the car screen, open **Apps** and choose **ExpeditionGauge**.
3. Keep the phone app running so gauges and recording stay live.

**After every upgrade:** Android Auto may cache the old app list. Force-stop Android Auto (or run `pwsh scripts/expedition/aa-refresh-host.ps1`), re-check Unknown sources + Customize launcher, then reconnect USB.

If the app does not appear: confirm Customize launcher is checked, reconnect USB, reboot the phone, and re-check **Unknown sources**. More detail: [`docs/help/ANDROID_AUTO.md`](docs/help/ANDROID_AUTO.md).

## Quick start

### Build release APK (reproducible)

```bash
export SOURCE_DATE_EPOCH=1700000000
python scripts/expedition/sync-app-icon.py
cd examples/android
./gradlew assembleRelease
```

CI verifies byte-identical `app-release-unsigned.apk` hashes. For device install, sign the unsigned output:

```powershell
pwsh scripts/expedition/sign-release-apk.ps1
```

Requires Android 8+ (API 26). Debug builds: `./gradlew assembleDebug`.

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
