# ExpeditionGauge — Android Auto install kit

## The problem

Downloading the APK from GitHub and tapping Install (or `adb install`) does **not** put ExpeditionGauge in Android Auto’s **Customize launcher** on modern phones. Android Auto requires Play Store install attribution for **both** installer and initiator.

## What to use

| Your phone | Use |
|------------|-----|
| **Rooted** (Magisk) + PC | `install-aa-from-pc.ps1` or `install-aa-from-pc.sh` (this zip) |
| **Unrooted, Android ≤ 13** | [KingInstaller](https://github.com/fcaronte/KingInstaller/releases) + this APK |
| **Unrooted, Android 14+** | Root + this kit, **or** wireless AA adapter (MITM/dev mode), **or** private Play Internal testing — KingInstaller / AAAD usually fail for arbitrary APKs |

Full guide (alternatives matrix): https://github.com/edwardlthompson/ExpeditionGauge/blob/main/docs/help/ANDROID_AUTO_SIDELOAD.md

## Rooted PC install (this kit)

1. Install [platform-tools](https://developer.android.com/tools/releases/platform-tools) (`adb` on PATH).
2. USB debugging on; connect phone; allow this computer.
3. Put `ExpeditionGauge-*.apk` in this folder (download from the same GitHub Release).
4. Run:

```powershell
pwsh .\install-aa-from-pc.ps1 -Apk .\ExpeditionGauge-2.14.1.apk
```

```bash
bash ./install-aa-from-pc.sh ExpeditionGauge-2.14.1.apk
```

5. Confirm both show Play Store:

```text
adb shell dumpsys package dev.foss.expeditiongauge | findstr /i "installerPackageName initiatingPackageName"
```

6. On phone: Android Auto → Unknown sources → Customize launcher → enable **ExpeditionGauge** → connect to car.

## Other options (no public Play listing, no Magisk)

- **Wireless adapter:** [AAWireless](https://www.aawireless.io/) developer mode, or FOSS [aa-proxy-rs](https://github.com/aa-proxy/aa-proxy-rs)
- **Private Play track:** Internal testing / Internal app sharing (real Play attribution)
- **Not for this APK:** [AAAD](https://github.com/shmykelsa/AAAD) only installs its curated catalog

There is **no** known FOSS phone app that, on stock Android 14+, can set `initiatingPackageName=com.android.vending` for an arbitrary APK without Magisk or Google Play’s installer.

## Files

- `install-aa-from-pc.ps1` — Windows
- `install-aa-from-pc.sh` — Linux/macOS/Git Bash
- `ANDROID_AUTO_SIDELOAD.md` — matrix + verification
- `ExpeditionGauge-*.apk` — include from the release when packaging
