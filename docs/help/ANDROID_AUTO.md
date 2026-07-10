# Android Auto setup (ExpeditionGauge)

ExpeditionGauge shows a **3-tile telemetry grid** on Android Auto (**Attitude** inclinometer, speed/heading/altitude, TPMS). There is **no in-app toggle** — the car UI connects automatically when your phone is linked to a compatible head unit. **No maps** are shown on Android Auto.

> **Platform limit:** Android Auto does not allow custom Compose/Canvas HUDs. The phone ball-in-ring G-meter is not mirrored; the head unit uses a **bitmap inclinometer** (`CarIcon`) on the Attitude tile instead. See [`AA_INCLINOMETER.md`](../design/AA_INCLINOMETER.md).

## Requirements

- Android phone with ExpeditionGauge installed (sideload or release APK)
- Car or head unit with **Android Auto** (wired USB or wireless, depending on vehicle)
- For **sideloaded** builds: Android Auto **developer mode** and **unknown sources** enabled on the phone

## Enable developer mode (sideload — no Play Store)

Do this **once** on the phone so Android Auto will list ExpeditionGauge after you install the APK from GitHub Releases:

1. Install ExpeditionGauge from a [GitHub Release](https://github.com/edwardlthompson/ExpeditionGauge/releases) APK (allow install from your browser/file manager if asked).
2. Open the **Android Auto** app on the phone.
3. Open the menu → **About** / **Version**, then tap the **version number about 10 times** until developer mode unlocks.
4. Menu → **Developer settings** → turn on **Unknown sources** (and allow ExpeditionGauge if prompted).
5. Connect to the car and open **ExpeditionGauge** from the head-unit Apps list.

Beginner walkthrough (same steps, more detail): [`README.md`](../../README.md#install-without-the-play-store-android-auto).

## Connect and launch

1. Connect the phone to the car (USB or wireless pairing per your vehicle).
2. On the head unit, open **Apps** → **ExpeditionGauge**.
3. You should see three tiles: **Attitude** (inclinometer graphic + P/R angles), **Telemetry**, and **TPMS**, plus **Record / Stop** and **Zero** actions.
4. Keep ExpeditionGauge running on the phone so sensors and recording stay active.

## Zero (set level)

**Zero** calibrates pitch/roll to the current attitude (same as phone **Calibrate**). Use on a **level surface** when parked or crawling slowly.

## Angle alerts

Configure **Settings → Alerts → Max pitch (°)** / **Max roll (°)** on the phone. When exceeded, the attitude tile shows a **red frame** on the head unit. Alert tones play on the phone (may route to car audio depending on head unit).

## Refresh rate

The app requests updates every **250 ms**; many head units still refresh near **~1 Hz** due to Android Auto host limits.

## Manual DHU / head unit checklist (M-003)

Use when `adb-smoke.ps1 -Scenario aa-inclinometer` reports `aa_host: disconnected` or for full sign-off:

1. Connect DHU or vehicle head unit; open **ExpeditionGauge** on Android Auto.
2. **Attitude** tile shows inclinometer bitmap (not ball-in-ring) with **P** and **R** angle lines only (no LatG).
3. Tilt phone — segments change color green → yellow → red; angles update (host may refresh ~1 Hz).
4. Tap **Zero** on level ground — pitch/roll near 0° on tile text.
5. Tap **Record** then **Stop** — session starts/stops on phone.
6. Set low **Max pitch** / **Max roll** in phone Settings → Alerts; tilt past limit — red frame on attitude tile.
7. **TPMS** tile shows corner text only (FL/FR/RL/RR), no vehicle graphic.

Phone-only automated checks: `pwsh scripts/expedition/adb-smoke.ps1 -Sprint 21 -Scenario aa-inclinometer -Serial b5214fc6`

## Manifest (developers)

`CarAppService` must declare a car app category in its intent-filter or the head unit will not list the app:

```xml
<category android:name="androidx.car.app.category.IOT" />
```

ExpeditionGauge uses **IOT** (grid of device/telemetry tiles). Also keep `automotive_app_desc.xml` with `<uses name="template" />`.

## Troubleshooting

| Symptom | Check |
|--------|--------|
| App not listed on head unit | Confirm APK includes `androidx.car.app.category.IOT`; developer mode + **Unknown sources**; Android Auto → Customize launcher; reinstall APK; reboot phone; reconnect AA |
| “Start ExpeditionGauge on phone” | Open the app on the phone while connected |
| Stale telemetry | Phone screen can sleep if **Keep screen awake** is off in Settings |
| TPMS shows `--` | Pair TPMS in phone Settings → TPMS |
| Zero does nothing | Phone fusion must be active (not external-only IMU path) |

## Privacy

Android Auto only displays telemetry already computed on the phone. No extra cloud upload is added for the car session.

See also: [ADR-0010](../adr/0010-android-auto.md), [AA_INCLINOMETER](../design/AA_INCLINOMETER.md), [CAR_GAUGE_PRIORITY](../design/CAR_GAUGE_PRIORITY.md).
