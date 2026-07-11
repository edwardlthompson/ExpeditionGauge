# Android Auto sideload matrix (ExpeditionGauge)

GitHub APK downloads alone are **not** enough for Customize launcher on modern Android Auto. AA checks install attribution:

| Field | Required value |
|-------|----------------|
| `installerPackageName` | `com.android.vending` (Play Store) |
| `initiatingPackageName` | `com.android.vending` (Play Store) |

Browser install / plain `adb install` / `pm install -i com.android.vending` usually leave `initiatingPackageName=com.android.shell` → **app never appears** in Customize launcher (confirmed on OnePlus 12 vs Car Scanner).

## Path A — Rooted phone + PC (recommended, proven)

Works on Magisk/rooted devices including Android 14/15/16.

1. Enable USB debugging; connect phone.
2. Download `ExpeditionGauge-*-AA-install-kit.zip` from [Releases](https://github.com/edwardlthompson/ExpeditionGauge/releases) **or** clone the repo.
3. Put `ExpeditionGauge-X.Y.Z.apk` next to the scripts (or pass `-Apk`).
4. Run:

```powershell
pwsh install-aa-from-pc.ps1 -Apk ExpeditionGauge-2.14.1.apk
```

```bash
bash install-aa-from-pc.sh ExpeditionGauge-2.14.1.apk
```

5. Confirm:

```text
adb shell dumpsys package dev.foss.expeditiongauge | grep -E "installerPackageName|initiatingPackageName"
```

Both lines must show `com.android.vending`.

6. Phone: Android Auto → developer mode → **Unknown sources** → **Customize launcher** → enable ExpeditionGauge → USB to car.

Repo equivalent: `pwsh scripts/expedition/aa-refresh-host.ps1 -Apk …`

## Path B — Unrooted phone (best effort)

| Android | Method | Notes |
|---------|--------|--------|
| ≤ 13 | [KingInstaller](https://github.com/fcaronte/KingInstaller/releases) “Install as king” | Enable **OnePlus/Oppo/Realme** option on those OEMs; then AA Unknown sources |
| 14+ | KingInstaller often **fails** (initiator becomes KingInstaller, not Play) | Need **root** (Path A) or a wireless AA adapter with its own bypass (e.g. AAWireless developer mode) — not FOSS |

There is **no** reliable unrooted FOSS-only trick on Android 14+ that sets `initiatingPackageName=com.android.vending`. Google closed the Intent spoof KingInstaller used.

## Path C — Already installed wrong

Uninstall ExpeditionGauge, then use Path A or B. Upgrading over a shell-initiated install usually keeps the bad initiator.

## Verify Customize launcher

After a good install, ExpeditionGauge must appear under Android Auto → **Customize launcher** (alphabetically under E). If it is missing, dumpsys initiator is still wrong — do not chase categories again.

## Privacy / trust

Path A uses `su` as the Play Store UID only to create the install session; the APK bytes are still the MIT-licensed build you downloaded. We do not redistribute Play Store or modify Google binaries.
