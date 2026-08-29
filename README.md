# ExpeditionGauge

<p align="center">
  <img src="docs/assets/app-icon-512.png" alt="ExpeditionGauge app icon" width="128" height="128" />
</p>

![MIT](https://img.shields.io/badge/license-MIT-2ea043?style=flat-square)
![Template](https://img.shields.io/badge/template-1.0.0-0969da?style=flat-square)
![FOSS](https://img.shields.io/badge/FOSS-no_tracking-656d76?style=flat-square)
[![CI](https://img.shields.io/github/actions/workflow/status/edwardlthompson/ExpeditionGauge/ci.yml?style=flat-square&label=CI)](https://github.com/edwardlthompson/ExpeditionGauge/actions/workflows/ci.yml)
![Android](https://img.shields.io/badge/Android-FOSS-3DDC84?style=flat-square)
![Version](https://img.shields.io/badge/version-2.17.0-0969da?style=flat-square)
![AGENT](https://img.shields.io/badge/AGENT-Cursor_Agent-2ea043?style=flat-square)
![HUMAN](https://img.shields.io/badge/HUMAN-Human_Developer-0969da?style=flat-square)
![ADB](https://img.shields.io/badge/ADB-Android_Device-bf8700?style=flat-square)
![AUTO](https://img.shields.io/badge/AUTO-CI_Scripts-656d76?style=flat-square)
![web](https://img.shields.io/badge/web-stack-646cff?style=flat-square)
![python](https://img.shields.io/badge/python-stack-3776AB?style=flat-square)
![android](https://img.shields.io/badge/android-stack-3DDC84?style=flat-square)

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
| **Android Auto** | Full-bleed 3×1 Drive HUD on the head unit (Attitude / Telemetry / TPMS) — see [`docs/help/ANDROID_AUTO.md`](docs/help/ANDROID_AUTO.md) |

Shipped through **v2.17.0**. See [`CHANGELOG.md`](CHANGELOG.md).

---

## Install with ADB (Play Store spoof)

ExpeditionGauge is FOSS and is **not** on the public Play Store. On modern phones, Android Auto only shows apps that look like they came from the Play Store. A normal “open the APK and tap Install” (or plain `adb install`) will put the app on your phone, but **Android Auto will hide it**.

The install method below uses a USB cable, a free tool called **ADB**, and a **Play Store spoof** so Android Auto accepts the app. It is the simplest universal path we support.

### What you need

1. A **Windows, macOS, or Linux** computer
2. A **USB cable** that can transfer data (not charge-only)
3. An Android phone with:
   - **USB debugging** enabled (steps below)
   - **Root** via Magisk (or another setup where `adb root` works) — required for the spoof
4. About 10–15 minutes

> **Why root?** Android Auto checks *who started the install*. Only a root (or `adb root`) session can create the install as the Play Store app (`com.android.vending`). Without that, Customize launcher stays empty.

---

### Step 1 — Turn on Developer options on the phone

1. Open **Settings**.
2. Tap **About phone** (sometimes under **System** → **About phone**).
3. Find **Build number**.
4. Tap **Build number** about **7 times** until you see a message like “You are now a developer!”
5. Go back, then open **Settings → System → Developer options**  
   (On some phones: **Settings → Additional settings → Developer options**.)

---

### Step 2 — Enable USB debugging

Still in **Developer options**:

1. Turn **USB debugging** **ON**.
2. (Recommended) Turn **Install via USB** / **USB debugging (Security settings)** **ON** if your phone shows those switches.
3. Plug the phone into the computer with the USB cable.
4. On the phone, when asked **Allow USB debugging?**, check **Always allow from this computer** and tap **Allow**.

---

### Step 3 — Install ADB on the computer

ADB (“Android Debug Bridge”) is a small command-line tool from Google.

#### Windows

1. Download [Platform Tools for Windows](https://developer.android.com/tools/releases/platform-tools) (zip from Google).
2. Unzip it somewhere easy, for example: `C:\platform-tools`
3. Open **PowerShell** or **Command Prompt**.
4. Go into that folder:

```powershell
cd C:\platform-tools
```

5. Check that ADB works and sees your phone:

```powershell
.\adb devices
```

You should see your device listed as `device` (not `unauthorized`). If it says `unauthorized`, unlock the phone and accept the USB debugging prompt again.

> Tip: keep this PowerShell/CMD window open — you will run all later commands from the folder that contains `adb.exe`, **or** add `C:\platform-tools` to your PATH.

#### macOS

```bash
brew install android-platform-tools
adb devices
```

(Or download [Platform Tools for Mac](https://developer.android.com/tools/releases/platform-tools) and run `./adb` from the unzipped folder.)

#### Linux

```bash
# Debian / Ubuntu
sudo apt update && sudo apt install android-tools-adb

adb devices
```

---

### Step 4 — Download the AA install kit

1. Open the latest [GitHub Release](https://github.com/edwardlthompson/ExpeditionGauge/releases).
2. Download **`ExpeditionGauge-X.Y.Z-AA-install-kit.zip`**  
   (example: `ExpeditionGauge-2.17.0-AA-install-kit.zip`).
3. Unzip it to a folder you can find easily, for example:
   - Windows: `C:\ExpeditionGauge-install`
   - macOS/Linux: `~/Downloads/ExpeditionGauge-install`

Inside the unzipped folder you should see at least:

- `ExpeditionGauge-X.Y.Z.apk`
- `aa-spoof-adb.sh`
- `bin/run-as-uid-arm64` (helper used on some phones)

---

### Step 5 — Sideload with the Play Store spoof (copy and paste)

Open a terminal:

- **Windows:** PowerShell or Command Prompt  
  Prefer **Git Bash** if you have [Git for Windows](https://git-scm.com/download/win) (easiest for the paste block below). You can also run `pwsh .\install-aa-from-pc.ps1` from the kit if that script is present.
- **macOS / Linux:** Terminal

1. Change into the unzipped kit folder (adjust the path):

```bash
cd /c/ExpeditionGauge-install
```

Windows PowerShell equivalent:

```powershell
cd C:\ExpeditionGauge-install
```

2. Confirm the phone is still connected:

```bash
adb devices
```

3. **Copy the entire block below**, paste it into the terminal, and press Enter.  
   Run this from the **AA-install-kit folder** (so `bin/run-as-uid-arm64` is found).

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

Easier alternative (same result): if `aa-spoof-adb.sh` is in the kit folder:

```bash
bash ./aa-spoof-adb.sh
```

Windows PowerShell alternative (from the kit folder):

```powershell
pwsh .\install-aa-from-pc.ps1
```

---

### Step 6 — Confirm the spoof worked

After the commands finish, you should see **two** lines similar to:

```text
installerPackageName=com.android.vending
initiatingPackageName=com.android.vending
```

**Both** must say `com.android.vending`.

On Windows Command Prompt / PowerShell (if you are not in Git Bash):

```powershell
adb shell dumpsys package dev.foss.expeditiongauge | findstr /i "installerPackageName initiatingPackageName"
```

If either line shows `com.android.shell` (or something else), Android Auto will hide the app — run Step 5 again and make sure `adb root` / Magisk is working.

---

### Step 7 — Enable ExpeditionGauge in Android Auto

1. On the phone, open the **Android Auto** app (or **Settings → Connected devices → Android Auto**).
2. Open the menu (⋮) → **About** / **Version**.
3. Tap the version / build line about **10 times** until developer mode unlocks.
4. Go back → menu → **Developer settings**.
5. Turn **Unknown sources** **ON**.
6. Go to Android Auto settings → **Customize launcher**.
7. Enable **ExpeditionGauge**.
8. Connect the phone to the car (USB preferred the first time), open **Apps**, and launch **ExpeditionGauge**. Keep the phone app running so sensors stay live.

Day-to-day tips: [`docs/help/ANDROID_AUTO.md`](docs/help/ANDROID_AUTO.md).

---

### Upgrading later

Every time you install a new version from GitHub:

1. Download the new **AA-install-kit** zip.
2. Repeat **Step 5** (spoof install) — do **not** use plain `adb install -r` or “Open with Package Installer”.
3. Re-check **Unknown sources** and **Customize launcher** if the app disappears from Android Auto.

---

## Privacy & security

- **Local-first** — session data stays on device unless you export or enable live telemetry
- **Loop recording** — oldest unprotected sessions auto-deleted when storage cap is reached; protect drives in session metadata
- **Auto-record** — optional start/stop when a bonded Bluetooth trigger device connects/disconnects (local ACL only)
- **`allowBackup=false`** — sessions are not included in Android cloud backup
- **Quiet network** — update checks run at most once per 24 hours (Settings can disable); live telemetry stays off until you enable it

See [`docs/PRIVACY.md`](docs/PRIVACY.md), [`SECURITY.md`](SECURITY.md), and [`docs/THREAT_MODEL.md`](docs/THREAT_MODEL.md).

## Build from source (developers)

```bash
export SOURCE_DATE_EPOCH=1700000000
python scripts/expedition/sync-app-icon.py
cd examples/android
./gradlew assembleRelease
```

Sign for device install:

```powershell
pwsh scripts/expedition/sign-release-apk.ps1
```

Then install with the **AA-install-kit / Step 5 spoof** above (not plain `adb install`).

Unit tests:

```bash
cd examples/android
./gradlew :app:testDebugUnitTest
```

## Agent development

This repo uses [agent-project-bootstrap](https://github.com/edwardlthompson/agent-project-bootstrap) scaffolding for Cursor agents.

| Doc | Purpose |
|-----|---------|
| [`docs/START_HERE.md`](docs/START_HERE.md) | Agent cold-start read order |
| [`BUILD_PLAN.md`](BUILD_PLAN.md) | Active sprint board |
| [`docs/CURSOR_MODES.md`](docs/CURSOR_MODES.md) | Ask / Plan / Agent / Debug routing |
| [`docs/help/BATCH_COMMANDS.md`](docs/help/BATCH_COMMANDS.md) | Slash commands (`/build`, `/verify`, `/ship`) |
| [`AGENTS.md`](AGENTS.md) | Router and session protocol |
| [`docs/BOOTSTRAP_ALIGNMENT.md`](docs/BOOTSTRAP_ALIGNMENT.md) | Template alignment gap log (0.15.x) |

How agents work: read START_HERE → pick Cursor mode → execute Sequential `[AGENT]` rows first; after each step run `python3 scripts/agent-run.py watch-agent-gates --once --autofix`. Prefer local compute (`.cursor/rules/local-compute.mdc`).

Resume the next task: `pwsh scripts/expedition/resume-agent.ps1`

## Repository layout

```
examples/android/   ExpeditionGauge app (Compose, Room, MapLibre)
docs/               Agent docs, design specs, ADRs, feature specs
scripts/            Gates, CI helpers, ADB smokes
```

## License

MIT — see [`LICENSE`](LICENSE).
