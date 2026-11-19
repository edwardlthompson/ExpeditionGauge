# News Inbox

Auto-collected inbox of news, upstream updates, security fixes, and ideas relevant to this repo, appended daily by News Bot.
A Cursor planning agent should review these items and fold the useful ones into BUILD_PLAN; entries are append-only, newest at the bottom.


## 2026-11-13

**News and updates**

- Media3 1.11.1 stable and 1.12.0-beta01 (lower buffer memory, JSpecify nullness, Compose test teardown crash fix). [source](https://developer.android.com/jetpack/androidx/releases/media3)
- Android Auto 18.2 beta teardown: Material 3 album-art theming for Car App Library templates, Gemini app-function hooks (unofficial). [source](https://www.androidsage.com/2026/11/13/android-auto-18-2/)
- xj-scanner: open-source reader for pre-OBD-II Jeep ECUs over Chrysler SCI with a $3 Arduino Nano. [source](https://github.com/MZULALI/xj-scanner)

**Tasks for the planning agent**

- [ ] Bump Media3 1.10.1 to 1.11.1 — Picks up playback fixes for recording playback. Label: `[AGENT]` · Priority: P3 · https://developer.android.com/jetpack/androidx/releases/media3
- [ ] Watch Car App Library template changes in Android Auto 18.2 — The :car module may need adjustments when 18.2 ships. Label: `[AGENT]` · Priority: P3 · https://www.androidsage.com/2026/11/13/android-auto-18-2/
- [ ] Consider cheap Arduino SCI telemetry for pre-OBD-II vehicles — Opens the HUD to older expedition vehicles. Label: `[AGENT]` · Priority: P3 · https://github.com/MZULALI/xj-scanner

## 2026-11-14

**News and updates**

- Android Auto 18.2 beta rolling out: 100+ assets reworked to prepare a light mode; Android 11 to become the minimum. [source](https://9to5google.com/2026/11/13/android-auto-18-2-update-beta-release/)

**Tasks for the planning agent**

- [ ] Check Drive HUD tiles stay readable in Android Auto light mode — Hardcoded dark colors could break when light mode ships. Label: `[AGENT]` · Priority: P3 · https://9to5google.com/2026/11/13/android-auto-18-2-update-beta-release/

## 2026-11-19

**News and updates**

- Jetpack Compose November '26 release is stable: Compose BOM 2026.11.01 (Compose 1.13) with Material3 1.5.0. The BOM needs Kotlin 2.2+ and compileSdk 37.1. Material3 1.5.0 breaks Slider/RangeSlider source (stateless overloads deprecated, `onValueChange` now required, RangeSliderState activeRangeStart/End renamed to startValue/endValue); SecureTextField now defaults to system obfuscation, and clickables play platform click sounds by default. [source](https://developer.android.com/blog/posts/jetpack-compose-november-2026-release) · [Material3 notes](https://developer.android.com/jetpack/androidx/releases/compose-material3)

**Tasks for the planning agent**

- [x] Bump Compose BOM 2026.06.01 to 2026.11.01 (Compose 1.13 / Material3 1.5.0) and fix Slider/RangeSlider call sites — Material3 1.5.0 makes `onValueChange` required on Slider/RangeSlider (4 files here call Slider/RangeSlider) and the BOM requires Kotlin 2.2+ and compileSdk 37.1, so a Dependabot/Argus BOM bump will fail unless AGP accepts 37.1 and the call sites are fixed first. Label: `[AGENT]` · Priority: P2 · https://developer.android.com/blog/posts/jetpack-compose-november-2026-release (synced to BUILD_PLAN)
