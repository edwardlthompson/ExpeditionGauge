# Android Auto setup (ExpeditionGauge)

ExpeditionGauge shows a **3-tile telemetry grid** on Android Auto (**Attitude** inclinometer, speed/heading/altitude, TPMS). There is **no in-app toggle** — the car UI connects automatically when your phone is linked to a compatible head unit. **No maps** are shown on Android Auto.

> **Platform limit:** Android Auto does not allow custom Compose/Canvas HUDs. The phone ball-in-ring G-meter is not mirrored; the head unit uses a **bitmap inclinometer** (`CarIcon`) on the Attitude tile instead. See [`AA_INCLINOMETER.md`](../design/AA_INCLINOMETER.md).

## Requirements

- Android phone with ExpeditionGauge installed (sideload or release APK)
- Car or head unit with **Android Auto** (wired USB or wireless, depending on vehicle)
- For **sideloaded** builds: Android Auto **developer mode** and **unknown sources** enabled on the phone

## Enable developer mode (sideload)

1. Open the **Android Auto** app on the phone (install from F-Droid / APK mirror if missing).
2. Tap the version number **10 times** to unlock developer settings.
3. Enable **Developer mode**.
4. Enable **Unknown sources** (or add `dev.foss.expeditiongauge` to the allowed apps list).

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

## Troubleshooting

| Symptom | Check |
|--------|--------|
| App not listed on head unit | Developer mode + unknown sources; reinstall APK; reboot phone |
| “Start ExpeditionGauge on phone” | Open the app on the phone while connected |
| Stale telemetry | Phone screen can sleep if **Keep screen awake** is off in Settings |
| TPMS shows `--` | Pair TPMS in phone Settings → TPMS |
| Zero does nothing | Phone fusion must be active (not external-only IMU path) |

## Privacy

Android Auto only displays telemetry already computed on the phone. No extra cloud upload is added for the car session.

See also: [ADR-0010](../adr/0010-android-auto.md), [AA_INCLINOMETER](../design/AA_INCLINOMETER.md), [CAR_GAUGE_PRIORITY](../design/CAR_GAUGE_PRIORITY.md).
