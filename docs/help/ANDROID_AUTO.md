# Android Auto setup (ExpeditionGauge)

ExpeditionGauge shows a **full-bleed Surface Drive HUD** on Android Auto (`NavigationTemplate`, no side content panel):

- **Wide** surface: native **3×1** Attitude | Telemetry | TPMS. Top: **Mute**, **Capture**, **Record/Stop**, parked-only **Level**. Tap the **left** attitude cube to cycle inclinometer styles → G-meter → 3D compass. A **permanent bottom DTC band** sits under the cubes (always reserved). When OBD Mode 03 (or debug sim) has codes, bold-red single-line text carousels each code (`n/N` + description, 5 s), **scaled to fit the row width** (shrink/expand; ellipsis only if still too long at minimum size), vertically centered in the band. Empty = blank band. Attitude taps ignore the footer.
- **Tall / split** surface (`H` clearly greater than `W`): native **1×2** Attitude | Telemetry only (TPMS tile omitted). **No** DTC footer. **No** Capture/Record/Level — host requires at least one ActionStrip action, so **Mute** stays as an icon-only control. Tap the **top** cube to cycle attitude modes. Map **PAN** remains so Surface taps work. Tire pressure **alerts/TTS still run** while the AA session holds sensors; only the TPMS cube is hidden.

The OEM chooses split-screen; EG cannot force a side pane. Leaving ExpeditionGauge on the head unit still ends the car Screen and sensor hold. There is **no in-app toggle** — the car UI connects automatically when your phone is linked. There is **no real map** — the Surface is a free canvas for gauges.

> **Platform note:** Projected AA blocks custom Compose views. Pane `setImage` is square-only (letterbox/crop). The Surface path needs Car API **7+**, `NAVIGATION_TEMPLATES`, `MAP_TEMPLATES`, and `ACCESS_SURFACE` (POI discovery category kept). See [`AA_INCLINOMETER.md`](../design/AA_INCLINOMETER.md) and [ADR-0010](../adr/0010-android-auto.md).

### Long-press / Level on the inclinometer

Android Auto does **not** support long-press on Surface content. **Level** is the parked-only ActionStrip action (same as phone calibrate / zero).

### DHU scripts (no manual head-unit server tap)

| Script | Purpose |
|--------|---------|
| `aa-start-head-unit-server.ps1` | Starts `DeveloperHeadUnitNetworkService` so port **5277** listens again after AA force-stop |
| `aa-refresh-host.ps1` | Play-spoof install + force-stop AA + **auto-restart** head unit server |
| `dhu-start-controlled.ps1` | Start DHU with named-pipe → stdin bridge (enables scripted `tap`, no mouse) |
| `dhu-console.ps1 -Command "tap x y"` | Send DHU console commands (Google’s stdin protocol) |
| `dhu-open-expeditiongauge.ps1` | Open EG via console `tap` when controlled; mouse fallback otherwise |
| `dhu-smoke.ps1` | Server + forward + **controlled** DHU + open ExpeditionGauge + capture `.cursor/screenshots/dhu-live.png` |
| `dhu-smoke.ps1 -Tall` | Same with `examples/android/car/config/dhu-tall.ini` (**720×1280**; Windows DHU ignores 480×800); capture `dhu-vertical-2cube.png`; restores prior `headunit.ini` |
| `capture-dhu-window.ps1` | Screenshot the DHU window only |

```powershell
pwsh scripts/expedition/aa-refresh-host.ps1 -Apk path\to\app-debug.apk
pwsh scripts/expedition/dhu-smoke.ps1 -RestartDhu

```

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
3. You should see a **wide 3×1 Drive HUD** (Attitude + Telemetry + TPMS) on a landscape Surface, or a **tall 1×2** (Attitude + Telemetry) when the host gives a tall/narrow rect (OEM split). Wide chrome: **Mute/Unmute**, **Capture**, **Record / Stop**, **Level** (parked-only). Tall mode has no app ActionStrip — mute on the phone.
4. Open ExpeditionGauge on the phone at least once so permissions are granted. While AA is connected **and EG is still the open car Screen**, sensors stay live even if the phone screen turns off (sensor hold). Leaving EG on the HU ends that hold. **Keep screen awake** still helps if you want the phone HUD visible.

## Level (set level)

**Level** (parked-only) calibrates pitch/roll/yaw to the current attitude (same as phone **Calibrate** / auto-calibrate confirm). While driving you should see the host’s parked-only message. Use on a **level surface** with the vehicle pointed forward.

## Angle alerts

Configure **Settings → Alerts → Max pitch (°)** / **Max roll (°)** on the phone. When exceeded, the attitude tile shows a **red frame** on the head unit and over-limit numbers turn **red + bold**. Alert TTS uses `USAGE_ASSISTANCE_NAVIGATION_GUIDANCE` **plus transient audio focus** (required so OEM AudioHardening does not mute `com.google.android.tts`); beeps use the music stream with the same focus pattern so DHU/car audio can duck media. Also leave **Settings → Audible alert tones** on and AA **Mute** off. Checklist: `pwsh scripts/expedition/aa-audio-smoke.ps1`.

## DHU mute smoke

```powershell
pwsh scripts/expedition/dhu-smoke.ps1 -Serial <adb-serial> -RestartDhu
# Confirm Mute is leftmost in the ActionStrip; toggle and verify Settings → Mute alert audio flips.
# Optional: copy capture to .cursor/screenshots/dhu-mute.png

```

## Refresh rate

The app requests updates every **500 ms** when attitude is changing (1 Hz when stable); many head units still refresh near **~1 Hz** due to Android Auto host limits.

## Desktop Head Unit preview (CLI — no Android Studio UI)

Projected AA cannot mirror the phone Compose HUD. Use DHU to preview the **Surface 3×1 Drive HUD** (Attitude | Telemetry | TPMS) while editing in Cursor.

**One-time SDK install** (CLI or Studio SDK Manager once):

```text
sdkmanager "extras;google;auto"

```

Binary lands under `%ANDROID_SDK_ROOT%\extras\google\auto\` (`desktop-head-unit.exe` on Windows).

**Day-to-day loop (Cursor + PowerShell):**

```powershell
# Optional: install/refresh after a build (Play Store spoof)
pwsh scripts/expedition/aa-refresh-host.ps1 -Serial <adb-serial> -Apk path\to\ExpeditionGauge.apk

# Smoke: head-unit server + DHU + open app + capture
pwsh scripts/expedition/dhu-smoke.ps1 -Serial <adb-serial> -RestartDhu

# Or forward + launch DHU only
pwsh scripts/expedition/dhu-preview.ps1 -Serial <adb-serial>

```

Flags (`dhu-preview`): `-InstallApk <apk>`, `-ForwardOnly`, `-InputMode rotary`, `-Headless`.

**Bitmap-only review (no phone/DHU):** render glance PNGs for Cursor:

```powershell
pwsh scripts/expedition/aa-bitmap-preview.ps1

```

Outputs under `.cursor/screenshots/aa-tile-*.png` / `dhu-live.png`.

Product validation path is **phone → Android Auto** (DHU or car). Native Compose on an aftermarket HU is not an active gate — see [`HEAD_UNIT_ROUTES.md`](HEAD_UNIT_ROUTES.md).
## Manual DHU checklist (M-003 — Desktop Head Unit)

Use when `adb-smoke.ps1 -Scenario aa-inclinometer` reports `aa_host: disconnected` or for full sign-off:

1. Connect DHU or vehicle head unit; open **ExpeditionGauge** on Android Auto.
2. **Attitude** cube cycles through inclinometer styles, G-meter, and 3D compass (tap left cube).
3. Tilt phone — segments change color green → yellow → red; angles update (host may refresh ~1 Hz).
4. With phone Offroad inclinometer visible **and** AA connected, tilt — neither surface should flash the wrong attitude.
5. Park; tap **Level** on level ground — pitch/roll near 0°.
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
| Level does nothing / parked message | Level is parked-only; also needs phone fusion (not external-only IMU). Toast: “Level needs phone sensors” |
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
- Optional: record phone AA version in [`HUMAN_BACKLOG.md`](../../HUMAN_BACKLOG.md); BUILD_PLAN **M-003** is closed via DHU

## Privacy

Android Auto only displays telemetry already computed on the phone. No extra cloud upload is added for the car session.

See also: [ADR-0010](../adr/0010-android-auto.md), [AA_INCLINOMETER](../design/AA_INCLINOMETER.md), [CAR_GAUGE_PRIORITY](../design/CAR_GAUGE_PRIORITY.md).
