# Android Auto sideload matrix (ExpeditionGauge)

GitHub APK downloads alone are **not** enough for Customize launcher on modern Android Auto. AA checks install attribution:

| Field | Required value |
|-------|----------------|
| `installerPackageName` | `com.android.vending` (Play Store) |
| `initiatingPackageName` | `com.android.vending` (Play Store) |

Browser install / plain `adb install` / `pm install -i com.android.vending` usually leave `initiatingPackageName=com.android.shell` → **app never appears** in Customize launcher (confirmed on OnePlus 12 vs Car Scanner).

## Path A — Rooted phone + PC (recommended, proven)

Works on Magisk/rooted devices including Android 14/15/16. Every GitHub Release ships:

| Asset | Purpose |
|-------|---------|
| `ExpeditionGauge-X.Y.Z.apk` | App binary |
| `ExpeditionGauge-X.Y.Z-AA-install-kit.zip` | Same APK + Play Store spoof scripts (`aa-spoof-adb.sh`, `install-aa-from-pc.ps1`) + `bin/run-as-uid-arm64` for `adb root` devices without Magisk `su` |

1. Enable USB debugging; connect phone.
2. Download and unzip `ExpeditionGauge-*-AA-install-kit.zip` from [Releases](https://github.com/edwardlthompson/ExpeditionGauge/releases) **or** clone the repo.
3. In the kit folder, run:

```powershell
pwsh install-aa-from-pc.ps1 -Apk ExpeditionGauge-2.16.3.apk
```

```bash
bash aa-spoof-adb.sh ExpeditionGauge-2.16.3.apk
```

Or paste the copy-paste ADB block from the [README](../../README.md#copy-paste-adb-spoof-git-bash--wsl--macos--linux).

4. Confirm:

```text
adb shell dumpsys package dev.foss.expeditiongauge | grep -E "installerPackageName|initiatingPackageName"
```

Both lines must show `com.android.vending`.

5. Phone: Android Auto → developer mode → **Unknown sources** → **Customize launcher** → enable ExpeditionGauge → USB to car.

Repo equivalent: `pwsh scripts/expedition/aa-refresh-host.ps1 -Apk …` · pack kit locally: `pwsh scripts/expedition/pack-aa-install-kit.ps1 -Apk ExpeditionGauge-X.Y.Z.apk`

## Path B — Unrooted phone (software)

| Android | Method | Notes |
|---------|--------|--------|
| ≤ 13 | [KingInstaller](https://github.com/fcaronte/KingInstaller/releases) “Install as king” | Enable **OnePlus/Oppo/Realme** option on those OEMs; then AA Unknown sources |
| 14+ | KingInstaller usually **fails** | Initiator becomes KingInstaller, not Play — Google closed the Intent spoof |

## Path C — Neither public Play listing nor Magisk (other real options)

These are the only known practical alternatives when you cannot root and cannot use public Play:

| Approach | What it is | Trade-offs |
|----------|------------|------------|
| **Wireless AA adapter + developer/MITM mode** | Commercial [AAWireless](https://www.aawireless.io/) (enable developer mode in companion app) or DIY FOSS [aa-proxy-rs](https://github.com/aa-proxy/aa-proxy-rs) on a Raspberry Pi / dongle | Extra hardware; adapter sits between phone and car and can present like DHU so sideloaded apps appear. aa-proxy-rs is FOSS but needs embedded setup |
| **Play Console Internal testing / Internal app sharing** | Upload APK/AAB to a **private** track; testers install via Play | Real Play attribution without a public store page; needs a Google Play developer account ($ one-time) and Play Protect still applies |
| **[AAAD](https://github.com/shmykelsa/AAAD)** | Phone installer for a **curated** third-party AA app list | Paid unlock; **not** a general installer for arbitrary GitHub APKs (ExpeditionGauge is not in that catalog). Flaky on Android 14+; Google patches often break it |
| **Desktop Head Unit / Headunit Reloaded** | PC or phone “fake car” | Fine for development; not a real head unit |
| **LSPosed + AA XLauncher Unlocked** | Hooks AA’s “installed from Play” checks | Still requires Magisk/root (+ Zygisk/LSPosed) |
| **Shizuku alone** | Elevated ADB-like APIs without full root | **Cannot** unlock third-party AA apps (Xposed needed for those hooks) |

### What does **not** work (do not chase)

- Changing Car App categories again (`IOT` / `POI` / dual / `NAVIGATION`) once attribution is wrong
- Browser “Install” from GitHub Releases on Android 14+
- Plain `adb install` or `pm install -i com.android.vending` (initiator stays shell)
- Expecting Unknown sources alone to list the app in Customize launcher
- Expecting AAAD / Fermata-style flows to install ExpeditionGauge (catalog apps only)

There is **no** known FOSS phone app that, on stock Android 14+, can set `initiatingPackageName=com.android.vending` for an arbitrary APK without Magisk or Google Play’s installer.

**Skip phone AA entirely:** aftermarket Android head units, OEM AAOS sideload, or DIY OpenAuto — see [`HEAD_UNIT_ROUTES.md`](HEAD_UNIT_ROUTES.md). The same APK (v2.14.2+) is prepared for those routes.

## Path D — Already installed wrong

Uninstall ExpeditionGauge, then use Path A, B, or C. Upgrading over a shell-initiated install usually keeps the bad initiator.

## Verify Customize launcher

After a good install, ExpeditionGauge must appear under Android Auto → **Customize launcher** (alphabetically under E). If it is missing, dumpsys initiator is still wrong.

## Privacy / trust

Path A uses `su` as the Play Store UID only to create the install session; the APK bytes are still the MIT-licensed build you downloaded. We do not redistribute Play Store or modify Google binaries.
