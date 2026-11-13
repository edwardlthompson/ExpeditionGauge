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
