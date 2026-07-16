# Android Auto setup (ExpeditionGauge)

ExpeditionGauge shows a **3-tile telemetry grid** on Android Auto (**Attitude** inclinometer, speed/heading/altitude, TPMS). There is **no in-app toggle** — the car UI connects automatically when your phone is linked to a compatible head unit. **No maps** are shown on Android Auto.

> **Platform limit:** Android Auto does not allow custom Compose/Canvas HUDs. The phone ball-in-ring G-meter is not mirrored; the head unit uses a **bitmap inclinometer** (`CarIcon`) on the Attitude tile instead. See [`AA_INCLINOMETER.md`](../design/AA_INCLINOMETER.md).

## Requirements

- Android phone with ExpeditionGauge installed (sideload or release APK)
- Car or head unit with **Android Auto** (wired USB or wireless, depending on vehicle)
- For **sideloaded** builds: Android Auto **developer mode**, **unknown sources**, and **Customize launcher** enabled on the phone

## Enable developer mode (sideload — no Play Store)

Do this **once** on the phone so Android Auto will list ExpeditionGauge after you install the APK from GitHub Releases:

1. Install ExpeditionGauge (see **Install so Customize launcher can see it** below — plain Downloads install is often not enough).
2. Open the **Android Auto** app on the phone.
3. Open the menu → **About** / **Version**, then tap the **version number about 10 times** until developer mode unlocks.
4. Menu → **Developer settings** → turn on **Unknown sources** (and allow ExpeditionGauge if prompted).
5. Back in Android Auto settings → **Customize launcher** → enable **ExpeditionGauge** (check the box).
6. Connect to the car and open **ExpeditionGauge** from the head-unit Apps list.

Beginner walkthrough: [`README.md`](../../README.md#install-without-the-play-store-android-auto).

**Other phones / GitHub sideload:** see [`ANDROID_AUTO_SIDELOAD.md`](ANDROID_AUTO_SIDELOAD.md) and the **AA-install-kit** zip on [Releases](https://github.com/edwardlthompson/ExpeditionGauge/releases) (rooted PC installer).

**Skip stock AA:** aftermarket HU / AAOS / MITM — [`HEAD_UNIT_ROUTES.md`](HEAD_UNIT_ROUTES.md).

## Install so Customize launcher can see it

Android Auto on **OnePlus** / recent AA builds hides Car App Library apps unless **both**:

- `installerPackageName=com.android.vending`
- `initiatingPackageName=com.android.vending`

Plain `adb install`, browser Downloads install, or even `pm install -i com.android.vending` leaves `initiatingPackageName=com.android.shell` — the app never appears under Customize launcher (verified vs Car Scanner on device).

**Preferred (rooted USB + computer):**

```powershell
pwsh scripts/expedition/aa-refresh-host.ps1 -Serial b5214fc6 -Apk ExpeditionGauge-2.14.2.apk

```

That creates the install session as the Play Store UID so both fields are `com.android.vending`.

**Verify:**

```text
adb shell dumpsys package dev.foss.expeditiongauge | findstr /i "installerPackageName initiatingPackageName category.POI"

```

You need `installerPackageName=com.android.vending`, `initiatingPackageName=com.android.vending`, and `category.POI`.

**Phone-only alternative (Android ≤ 13):** [KingInstaller](https://github.com/fcaronte/KingInstaller/releases) (“Install as king”, enable the **OnePlus/Oppo/Realme** option if needed). Still enable AA Unknown sources afterward.

**Android 14+ without Magisk:** no reliable phone-only spoof for an arbitrary APK — see the alternatives matrix in [`ANDROID_AUTO_SIDELOAD.md`](ANDROID_AUTO_SIDELOAD.md) (wireless MITM adapter, private Play track).

## After every install or upgrade (required)

Android Auto caches the discovered app list. Install-attribution changes are invisible until the host refreshes:

**Preferred (ADB):**

```powershell
pwsh scripts/expedition/aa-refresh-host.ps1 -Serial b5214fc6 -Apk ExpeditionGauge-2.14.2.apk

```

**Manual minimum:** force-stop Android Auto → open ExpeditionGauge once → reboot phone → re-enable Unknown sources → Customize launcher → USB reconnect.

**Full reset (clears AA settings):** add `-Clear` to `aa-refresh-host.ps1` — then unlock developer mode and Unknown sources again.

## Connect and launch

1. Connect the phone to the car (**USB preferred** for first successful discover; wireless after that).
2. On the head unit, open **Apps** → **ExpeditionGauge**.
3. You should see three tiles: **Attitude** (inclinometer graphic + single-line P/R text), **Telemetry**, and **TPMS**, plus **Record / Stop** (titled + icon) and **Zero** (level icon — icon-only; parked-only; GridTemplate allows only one custom title).
4. Open ExpeditionGauge on the phone at least once so permissions are granted. While AA is connected, sensors stay live even if the phone screen turns off (sensor hold). **Keep screen awake** still helps if you want the phone HUD visible.

## Zero (set level)

**Zero** (level / diamond icon on the action strip — not a titled button) calibrates pitch/roll/yaw to the current attitude (same as phone **Calibrate** / auto-calibrate confirm). The host only runs Zero while **parked**; while driving you should see the host’s parked-only message. Use on a **level surface** with the vehicle pointed forward. Attitude labels show whole degrees; the inclinometer graphic stays smooth.

## Angle alerts

Configure **Settings → Alerts → Max pitch (°)** / **Max roll (°)** on the phone. When exceeded, the attitude tile shows a **red frame** on the head unit. Alert tones play on the phone (may route to car audio depending on head unit).

## Refresh rate

The app requests updates every **500 ms** when attitude is changing (1 Hz when stable); many head units still refresh near **~1 Hz** due to Android Auto host limits.

## Manual DHU / head unit checklist (M-003)

Use when `adb-smoke.ps1 -Scenario aa-inclinometer` reports `aa_host: disconnected` or for full sign-off:

1. Connect DHU or vehicle head unit; open **ExpeditionGauge** on Android Auto.
2. **Attitude** tile shows inclinometer bitmap (not ball-in-ring) with single-line **P** / **R** text.
3. Tilt phone — segments change color green → yellow → red; angles update (host may refresh ~1 Hz).
4. With phone Offroad inclinometer visible **and** AA connected, tilt — neither surface should flash the wrong attitude.
5. Park; tap the **Zero** level icon on level ground — pitch/roll near 0° on tile text.
6. Tap **Record** then **Stop** — session starts/stops on phone. With storage cap full, expect a toast (no process crash).
7. Turn phone screen off while AA stays up — tiles should keep updating (sensor hold).
8. Set low **Max pitch** / **Max roll** in phone Settings → Alerts; tilt past limit — red frame on attitude tile.
9. **TPMS** tile shows FL/FR/RL/RR on one truncated line (may ellipsize on narrow clusters).

Phone-only automated checks: `pwsh scripts/expedition/adb-smoke.ps1 -Sprint 21 -Scenario aa-inclinometer -Serial b5214fc6`

## Manifest (developers)

`CarAppService` must declare **one** car app category in its intent-filter or the head unit will not list the app:

```xml
<category android:name="androidx.car.app.category.POI" />

```

ExpeditionGauge uses **POI** for **sideload / projected Android Auto** discovery. Many cars filter **IOT** even with Unknown sources (DHU is more permissive). This is an intentional FOSS trade-off — **not Play-certified**. Do **not** add NAVIGATION or dual categories. Also keep `automotive_app_desc.xml` with `<uses name="template" />`.

## Troubleshooting

| Symptom | Check |
|--------|--------|
| App not listed on head unit | Confirm dumpsys shows `category.POI`; developer mode + **Unknown sources**; **Customize launcher** checked; run `aa-refresh-host.ps1`; reinstall APK; reboot phone; prefer USB reconnect |
| Not in Customize launcher at all | Confirm `installerPackageName=com.android.vending` in dumpsys; re-run `aa-refresh-host.ps1 -Apk …`; Unknown sources on; then Escalation |
| Listed in Customize but missing on car | Force-stop AA; USB cable/port; disable battery restriction on ExpeditionGauge + Android Auto |
| “Open ExpeditionGauge on phone” | Open the app once for permissions; AA keeps sensors via sensor hold after that |
| Stale telemetry | Confirm AA session is connected (sensor hold); grant motion/location permissions; Prefer **Keep screen awake** if you also want the phone HUD |
| TPMS shows `--` | Pair TPMS in phone Settings → TPMS |
| Zero does nothing / parked message | Zero is parked-only; also needs phone fusion (not external-only IMU). Toast: “Zero needs phone sensors” |
| Record toast “Storage full” | Free or unprotect a session on the phone; must **not** crash the process |
| App crashes when opening on HU | Check Settings → Android Auto → Last crash, or `adb logcat -b crash` (release APKs are non-debuggable so `run-as` may fail). Known causes: (1) `Action list exceeded max number of 1 actions with custom titles` — GridTemplate strip must keep only **one** titled action (Record/Stop); Zero is icon-only. (2) `IllegalStateException: When a grid item is loading, the image must not be set and vice versa` — fixed by always setting GridItem images. (3) Uncaught `StorageCapBlockedException` on Record — fixed by async `runCatching` bridge. |
| Wrong layout after rotating phone | AA layout uses HU config only; phone portrait + landscape HU is supported. Confirm P/R still match vehicle frame |
## Escalation (stop-rule — no more category swaps)

If ExpeditionGauge is still **absent from Customize launcher** after:

1. Install **≥ 2.14.2** with `category.POI` **and** `installerPackageName=com.android.vending`
2. `aa-refresh-host.ps1 -Apk …` (and optional `-Clear`)
3. Unknown sources + Customize launcher
4. USB reconnect

…treat it as **OEM / host policy**, not an app-category bug. **Do not** try MESSAGING, WEATHER, dual categories, or NAVIGATION.

Then only:

- Update Android Auto from Play; reboot phone
- Prefer USB over wireless for discovery
- Known-good cable/port; unrestricted battery for ExpeditionGauge + Android Auto
- Confirm Unknown sources survived reboot
- Record phone AA version + head-unit model in [`HUMAN_BACKLOG.md`](../../HUMAN_BACKLOG.md) and leave BUILD_PLAN **M-003** open

## Privacy

Android Auto only displays telemetry already computed on the phone. No extra cloud upload is added for the car session.

See also: [ADR-0010](../adr/0010-android-auto.md), [AA_INCLINOMETER](../design/AA_INCLINOMETER.md), [CAR_GAUGE_PRIORITY](../design/CAR_GAUGE_PRIORITY.md).
