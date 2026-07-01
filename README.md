# ExpeditionGauge

![MIT](https://img.shields.io/badge/license-MIT-2ea043?style=flat-square)
![Android](https://img.shields.io/badge/Android-FOSS-3DDC84?style=flat-square)
![Version](https://img.shields.io/badge/version-2.9.1-0969da?style=flat-square)

Offline-first automotive HUD for off-road and track driving — Compose gauges, GPS/IMU fusion, BLE sensors, session recording, and playback with export.

**Package:** `dev.foss.expeditiongauge` · **License:** MIT · **No Google Play Services or Firebase in the APK.**

## Features

| Area | Capabilities |
|------|----------------|
| **Live HUD** | Speed, G-forces, drift angle (β), pitch/roll, configurable gauge layouts |
| **Sensors** | Phone IMU/GPS, BLE IMU, TPMS, external NMEA GPS, OBD-II (Classic Bluetooth) |
| **Recording** | Room-backed sessions, alerts, lap timing, crawling mode, thermal-aware logging |
| **Playback** | MapLibre route map, scrubber, elevation profile, ghost lap, media markers |
| **Export & share** | GPX/ZIP, playback video burn-in, 3D flyover MP4, stats card share sheet |
| **Live telemetry** | Opt-in P2P sender/receiver (WebSocket signaling) |
| **Android Auto** | Live metrics via AndroidX Car App Library (user-installed host app) |

Shipped through **v2.9.1** — Relive wave (media → elevation → library → video export → 3D flyover → sharing). See [`CHANGELOG.md`](CHANGELOG.md) and [`COMPLETED_TASKS.md`](COMPLETED_TASKS.md).

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

Scenario list: `scripts/expedition/adb-smoke.ps1` (Relive scenarios in `adb-scenarios/relive.ps1`).

## Privacy & security

- **Local-first** — session data stays on device unless you export or enable live telemetry
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
scripts/expedition/ ADB smokes, sprint sign-off, release helpers
modules/android/    Stack-specific agent rules
```

## Distribution

- **GitHub Releases** — primary channel ([`edwardlthompson/ExpeditionGauge`](https://github.com/edwardlthompson/ExpeditionGauge))
- **F-Droid** — metadata under `examples/android/metadata/` (submission optional)

## Contributing

MIT licensed. See [`CONTRIBUTING.md`](CONTRIBUTING.md).

Built on agent-project-bootstrap **0.11.1** (template maintainer docs: [`docs/MAINTAINING_THE_TEMPLATE.md`](docs/MAINTAINING_THE_TEMPLATE.md)).
