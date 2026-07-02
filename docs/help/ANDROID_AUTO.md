# Android Auto setup (ExpeditionGauge)

ExpeditionGauge shows a **3-tile telemetry grid** on Android Auto (G-meter, speed/heading/altitude, TPMS). There is **no in-app toggle** — the car UI connects automatically when your phone is linked to a compatible head unit.

> **Platform limit:** Android Auto does not allow custom Compose/Canvas HUDs. The phone dashboard cannot be mirrored pixel-for-pixel; the head unit uses structured `GridTemplate` tiles instead.

## Requirements

- Android phone with ExpeditionGauge installed (sideload or release APK)
- Car or head unit with **Android Auto** (wired USB or wireless, depending on vehicle)
- For **sideloaded** builds: Android Auto **developer mode** and **unknown sources** enabled on the phone

## Enable developer mode (sideload)

1. Open the **Android Auto** app on your phone (install from F-Droid / APK mirror if missing).
2. Tap the version number **10 times** to unlock developer settings.
3. Enable **Developer mode**.
4. Enable **Unknown sources** (or add `dev.foss.expeditiongauge` to the allowed apps list).

## Connect and launch

1. Connect the phone to the car (USB or wireless pairing per your vehicle).
2. On the head unit, open **Apps** → **ExpeditionGauge**.
3. You should see three tiles: **G-meter**, **Telemetry**, and **TPMS**, plus **Record / Stop** and **Mark** actions.
4. Keep ExpeditionGauge running on the phone so sensors and recording stay active.

## Troubleshooting

| Symptom | Check |
|--------|--------|
| App not listed on head unit | Developer mode + unknown sources; reinstall APK; reboot phone |
| “Start ExpeditionGauge on phone” | Open the app on the phone while connected |
| Stale telemetry | Phone screen can sleep if **Keep screen awake** is off in Settings |
| TPMS shows `--` | Pair TPMS in phone Settings → TPMS |

## Privacy

Android Auto only displays telemetry already computed on the phone. No extra cloud upload is added for the car session.

See also: [ADR-0010](../adr/0010-android-auto.md), [CAR_GAUGE_PRIORITY](../design/CAR_GAUGE_PRIORITY.md).
