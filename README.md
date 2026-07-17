# ExpeditionGauge

<p align="center">
  <img src="docs/assets/app-icon-512.png" alt="ExpeditionGauge app icon" width="128" height="128" />
</p>

![MIT](https://img.shields.io/badge/license-MIT-2ea043?style=flat-square)
![Android](https://img.shields.io/badge/Android-FOSS-3DDC84?style=flat-square)
![Version](https://img.shields.io/badge/version-2.14.2-0969da?style=flat-square)

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
| **Other head units** | Aftermarket Android HU, AAOS sideload, wireless MITM, DIY OpenAuto — [`docs/help/HEAD_UNIT_ROUTES.md`](docs/help/HEAD_UNIT_ROUTES.md) |

Shipped through **v2.14.2** — soft hardware features for HU/AAOS install, AA head-unit discovery (`category.POI`), landscape inclinometer. See [`CHANGELOG.md`](CHANGELOG.md).

## Other head-unit routes (no public Play Store)

If stock Android Auto on the phone is too locked down, use a display path Google does not gate the same way. Full matrix: [`docs/help/HEAD_UNIT_ROUTES.md`](docs/help/HEAD_UNIT_ROUTES.md).

| Route | What to buy / use | How ExpeditionGauge runs |
|-------|-------------------|---------------------------|
| **Aftermarket Android HU** | ATOTO S8/X10-class, Mekede/Dasaita, Joying, Xtrons, similar unlocked radios | Sideload the APK **on the head unit** — full Compose HUD (best FOSS car path) |
| **Wireless AA MITM** | [AAWireless](https://www.aawireless.io/) (developer mode) or FOSS [aa-proxy-rs](https://github.com/aa-proxy/aa-proxy-rs) | Phone keeps the app; adapter presents like DHU so the **Car App** grid can appear |
| **DIY Pi / OpenAuto** | OpenAuto, Crankshaft, or Android-on-SBC | Usually phone → DIY AA head unit (same Car App path); native Android SBCs = treat like aftermarket HU |
| **OEM Android Automotive** | Polestar 2/3/4, Volvo EX30/EX90/XC40 Recharge (most sideload-friendly); others vary | Sideload APK **on the car**; park for permissions; external BLE/NMEA if no IMU |
| **Stock AA on phone** | Magisk kit / KingInstaller ≤13 / private Play track | See [Install without the Play Store](#install-without-the-play-store-android-auto) below |

**App readiness (v2.14.2+):** one APK serves all routes — soft `uses-feature` so HUs without phone sensors can install; `distractionOptimized` for AAOS; projected `CarAppService` for MITM/AA; BLE IMU / NMEA / OBD when the unit has no usable sensors. Enable **Keep screen awake** on dash mounts.

## Install without the Play Store (Android Auto)

ExpeditionGauge is FOSS and is **not** on the public Play Store. Android Auto will **not** list a normal GitHub sideload on modern phones — it requires Play Store install attribution (`installerPackageName` **and** `initiatingPackageName` = `com.android.vending`).

Download assets from [Releases](https://github.com/edwardlthompson/ExpeditionGauge/releases):

- `ExpeditionGauge-X.Y.Z.apk` — the app (phone / HU / AAOS)
- `ExpeditionGauge-X.Y.Z-AA-install-kit.zip` — **Play Store spoof sideload kit**: same APK + `aa-spoof-adb.sh` / `install-aa-from-pc.ps1` + `bin/run-as-uid-arm64` helper + guide

Full matrix: [`docs/help/ANDROID_AUTO_SIDELOAD.md`](docs/help/ANDROID_AUTO_SIDELOAD.md) · day-to-day AA use: [`docs/help/ANDROID_AUTO.md`](docs/help/ANDROID_AUTO.md)

### Pick an install path

| Your situation | What to do |
|----------------|------------|
| **Rooted phone** (Magisk / `adb root`) + PC | Unzip the **AA-install-kit** and run the spoof script (Path A) — proven on Android 14/15/16 |
| **Unrooted, Android ≤ 13** | [KingInstaller](https://github.com/fcaronte/KingInstaller/releases) “Install as king” (enable OnePlus/Oppo/Realme option if needed) |
| **Unrooted, Android 14+** | No reliable software-only spoof. Use **root** (Path A), a **wireless AA adapter** with developer/MITM mode, or a **private Play track** (see alternatives below) |

### Path A — Rooted PC install (recommended)

1. USB debugging on; connect the phone; allow this computer.
2. Download and unzip `ExpeditionGauge-*-AA-install-kit.zip` from [Releases](https://github.com/edwardlthompson/ExpeditionGauge/releases).
3. In that folder, run **one** of:

```powershell
pwsh .\install-aa-from-pc.ps1 -Apk .\ExpeditionGauge-2.16.3.apk
```

```bash
bash ./aa-spoof-adb.sh ExpeditionGauge-2.16.3.apk
```

From a full clone: `pwsh scripts/expedition/aa-refresh-host.ps1 -Apk ExpeditionGauge-2.16.3.apk`

#### Copy-paste ADB spoof (Git Bash / WSL / macOS / Linux)

Unzip the AA-install-kit, `cd` into it, then paste:

```bash
# Play Store spoof install — rooted phone + USB adb
# Sets installerPackageName AND initiatingPackageName to com.android.vending
APK=$(ls ExpeditionGauge-*.apk | head -1)
adb root; sleep 1
adb push "$APK" /data/local/tmp/ExpeditionGauge-aa-install.apk
UID=$(adb shell cmd package list packages -U com.android.vending | sed -n 's/.*uid:\([0-9]*\).*/\1/p' | head -1 | tr -d '\r')
SIZE=$(adb shell stat -c %s /data/local/tmp/ExpeditionGauge-aa-install.apk | tr -d '\r')
CREATE=$(adb shell "su $UID -c \"pm install-create --user 0 -i com.android.vending -r -S $SIZE\"" 2>&1 | tr -d '\r')
if ! echo "$CREATE" | grep -q '\[[0-9]\+\]'; then
  adb push bin/run-as-uid-arm64 /data/local/tmp/run-as-uid
  adb shell chmod 755 /data/local/tmp/run-as-uid
  CREATE=$(adb shell "/data/local/tmp/run-as-uid $UID pm install-create --user 0 -i com.android.vending -r -S $SIZE" | tr -d '\r')
fi
SID=$(echo "$CREATE" | sed -n 's/.*\[\([0-9]*\)\].*/\1/p')
adb shell pm install-write -S "$SIZE" "$SID" base /data/local/tmp/ExpeditionGauge-aa-install.apk
adb shell pm install-commit "$SID"
adb shell rm -f /data/local/tmp/ExpeditionGauge-aa-install.apk
adb shell am force-stop com.google.android.projection.gearhead
adb shell dumpsys package dev.foss.expeditiongauge | grep -E 'installerPackageName|initiatingPackageName'
```

Both printed lines must be `com.android.vending`. Prefer `bash ./aa-spoof-adb.sh` from the kit if you want the same logic with clearer errors.

4. Confirm (Windows `cmd` / PowerShell):

```text
adb shell dumpsys package dev.foss.expeditiongauge | findstr /i "installerPackageName initiatingPackageName"
```

### Path B — After install: enable AA + Customize launcher

1. Open **Android Auto** on the phone → menu → **About** / **Version** → tap the version **~10 times** (developer mode).
2. Menu → **Developer settings** → **Unknown sources** ON.
3. Android Auto settings → **Customize launcher** → enable **ExpeditionGauge**.
4. USB to the car (preferred first time) → **Apps** → **ExpeditionGauge**. Keep the phone app running.

**After every upgrade:** reinstall with the AA-install-kit spoof (or KingInstaller on ≤13) — plain `adb install -r` will hide the app again — then re-check Unknown sources + Customize launcher.

### Are there other options besides Play Store or root?

**Short answer:** nothing simple and FOSS that works on a stock Android 14+ phone over a normal USB cable. Community options:

| Approach | Root? | Notes |
|----------|-------|--------|
| **Wireless AA adapter + developer/MITM mode** | No | Commercial [AAWireless](https://www.aawireless.io/) (enable developer mode in its app) or DIY FOSS [aa-proxy-rs](https://github.com/aa-proxy/aa-proxy-rs) on a Pi/dongle — presents like DHU and can show sideloaded apps |
| **Google Play Internal testing / Internal app sharing** | No | Upload the APK/AAB to a **private** Play Console track; testers install “from Play” so attribution is real. Needs a Play developer account; not a public listing |
| **KingInstaller** | No | Works on many **Android ≤ 13** phones; usually **fails on 14+** (initiator becomes KingInstaller) |
| **[AAAD](https://github.com/shmykelsa/AAAD)** | No | Installs a **curated catalog** of third-party AA apps (paid unlock). Not a general “any GitHub APK” installer for ExpeditionGauge. Unreliable on Android 14+; maintainer often points people at AAWireless |
| **Desktop Head Unit / Headunit Reloaded** | No | Dev/emulator path — not a substitute for a real car head unit |
| **LSPosed / AA XLauncher Unlocked** | Yes (Magisk) | Hooks AA’s Play checks; still root |
| **Shizuku alone** | No | **Cannot** unlock third-party AA apps (hooks need Xposed) |

**Bottom line for ExpeditionGauge on a stock Android 14+ phone:** use Magisk + the AA install kit, a wireless MITM adapter, or a private Play track. There is no reliable FOSS phone-only trick that sets `initiatingPackageName=com.android.vending` for an arbitrary APK. If Customize launcher stays empty, dumpsys initiator is still wrong — do not change car-app categories again.

Prefer skipping phone AA entirely? Use an [aftermarket Android head unit or AAOS sideload](#other-head-unit-routes-no-public-play-store) — [`docs/help/HEAD_UNIT_ROUTES.md`](docs/help/HEAD_UNIT_ROUTES.md).

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
