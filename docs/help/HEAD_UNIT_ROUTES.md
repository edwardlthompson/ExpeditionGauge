# Head-unit routes (beyond stock Android Auto)

Stock Android Auto on modern phones blocks normal GitHub sideloads (Play install attribution). These routes get ExpeditionGauge onto a dash display **without** relying on the public Play Store.

Same APK (`ExpeditionGauge-*.apk`) is intended for all routes below. From **v2.14.2**: hardware features are soft (installable on HUs without phone IMU), `MainActivity` is distraction-optimized for AAOS, and the projected **Car App** path still serves wireless MITM / AA.

| Route | Where the UI runs | Needs Play attribution? | App readiness |
|-------|-------------------|-------------------------|---------------|
| **A. Aftermarket Android head unit** | Native Compose HUD on the HU | No — sideload APK on the unit | Ready — full phone UI; enable **Keep screen awake**; pair BLE IMU/GPS/OBD if the HU has no sensors |
| **B. Wireless AA MITM adapter** | Phone Car App → car (via adapter) | Often bypassed by adapter “developer / DHU” mode | Ready — `CarAppService` + `category.POI` + `HostValidator.ALLOW_ALL` |
| **C. DIY OpenAuto / Pi-style HU** | Usually phone Car App → DIY head unit | Same as phone AA / MITM | Ready — same projected AA path as B |
| **D. OEM Android Automotive (AAOS)** | Native app **on the car** | No Play phone-attribution; car may still gate unknown sources | Ready to **install** (soft `type.automotive`, distraction-optimized). Full AAOS UX polish is best-effort — park to grant permissions; use external sensors if the car has no usable IMU |
| **E. Magisk / private Play / KingInstaller** | Phone Car App → stock AA | Yes (or root spoof) | Ready — see [`ANDROID_AUTO_SIDELOAD.md`](ANDROID_AUTO_SIDELOAD.md) |

## A — Aftermarket Android head unit (recommended FOSS path)

**Examples:** ATOTO S8/X10 class, Mekede/Dasaita, Joying, Xtrons, and similar unlocked Android radios.

1. Enable **Unknown apps** / ADB on the head unit.
2. Install `ExpeditionGauge-*.apk` from [Releases](https://github.com/edwardlthompson/ExpeditionGauge/releases) (USB stick, file manager, or `adb install`).
3. Open the app → Settings → enable **Keep screen awake**.
4. If the unit has weak/no IMU or GPS, pair **BLE IMU**, **NMEA GPS**, and/or **OBD-II** from phone Settings (same as on a phone).

This bypasses Google’s Android Auto projection gate entirely: the HUD is a normal Android app on the dash.

## B — Wireless AA adapter (MITM / developer mode)

**Examples:** [AAWireless](https://www.aawireless.io/) (enable developer mode in its app), FOSS [aa-proxy-rs](https://github.com/aa-proxy/aa-proxy-rs).

1. Install ExpeditionGauge on the **phone** (GitHub APK is fine for many adapters; Magisk kit still safest for stock AA).
2. Enable Android Auto **Unknown sources** + **Customize launcher** on the phone when the adapter still enforces them.
3. Connect phone → adapter → car. Open **ExpeditionGauge** from the car Apps list.

The phone keeps running sensors; the car shows the 3-tile Car App grid (not the full Compose cube).

## C — DIY OpenAuto / Crankshaft / Pi HUD

Treat like **B**: the Pi (or SBC) acts as an Android Auto head unit. Install and configure the phone for projected AA (sideload docs), then connect to the DIY unit. Native Android-on-Pi boards that sideload APKs behave like **A**.

## D — OEM Android Automotive OS

**More sideload-friendly OEMs (community reports):** Polestar 2/3/4, Volvo EX30 / EX90 / XC40 Recharge (same Google-built-in family). GM / Honda / others vary and are often locked down.

1. Enable developer options on the car if available; allow install from the file manager / AnExplorer / USB.
2. Sideload the same ARM64 APK.
3. Grant location / Bluetooth while **parked**; enable Keep screen awake.
4. Prefer external BLE/NMEA sensors if built-in phone-style IMU is missing.

ExpeditionGauge is **not** a Play-certified AAOS app. Driving-time restrictions are OEM policy — do not bypass safety locks.

## E — Stay on stock Android Auto

Use the Magisk AA install kit, KingInstaller (≤13), or a private Play track — [`ANDROID_AUTO_SIDELOAD.md`](ANDROID_AUTO_SIDELOAD.md) and the README Path A/B section.

## What the APK already provides for these routes

| Capability | Why it matters |
|------------|----------------|
| Soft `uses-feature` (IMU, GPS, BT, telephony, `type.automotive`) | Install does not fail on HUs/AAOS missing phone hardware |
| `distractionOptimized` on `MainActivity` | AAOS launchers can show the HUD while driving (OEM may still restrict) |
| `resizeableActivity` + `fullUser` orientation | Wide / odd HU aspect ratios |
| Compose phone HUD | Routes A and D (native on device) |
| `CarAppService` POI templates | Routes B, C, E (projected AA) |
| BLE IMU / NMEA / OBD | Head units without usable built-in sensors |
| Keep screen awake setting | Dash mounts that would otherwise dim |

## Not supported as a product goal

- Replacing Google Android Auto inside locked OEM radios without MITM hardware
- Shipping a second “AAOS-only” store listing (FOSS sideload first)
- Claiming Play certification for `category.POI`

See also: [`ANDROID_AUTO.md`](ANDROID_AUTO.md), [`ANDROID_AUTO_SIDELOAD.md`](ANDROID_AUTO_SIDELOAD.md), [ADR-0010](../adr/0010-android-auto.md).
