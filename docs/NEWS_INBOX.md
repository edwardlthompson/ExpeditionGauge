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

## 2026-11-20

**News and updates**

- Gradle 9.9.2 and 8.14.8 (Nov 19) fix four High-severity build-tool bugs: SFTP repositories didn't verify host keys, transitive snapshot dependencies skipped dependency verification, TestKit used a predictable temp directory, and the daemon registry was briefly readable by other local users. 9.9.2 also fixes a 9.9.0 plugin-resolution regression with registries that answer 401. Wrappers older than 9.9.1/8.14.7 also miss the earlier Critical fix (version-catalog values run as code). [9.9.2 notes](https://docs.gradle.org/9.9.2/release-notes.html) · [8.14.8 notes](https://docs.gradle.org/8.14.8/release-notes.html)
- Android developer verification goes global from January 2027 and will cover all install sources on certified devices: it starts with Play and six other distributors, then expands country by country over several months. Developers who ship only outside Play are told to create an Android Developer Console account and register package names plus signing-key SHA-256 fingerprints (a new open-source guide covers re-signing repos such as F-Droid). Unregistered apps stay installable only through a one-time "advanced flow" on each device; hobbyist accounts now allow 50 devices. F-Droid publicly opposes the scheme. [Android blog](https://developer.android.com/blog/posts/android-developer-verification-2027) · [open-source registration guide](https://developer.android.com/developer-verification/guides/open-source-app-registration) · [F-Droid open letter](https://f-droid.org/2026/02/24/open-letter-opposing-developer-verification.html)

**Tasks for the planning agent**

- [x] Bump the Gradle wrapper 9.6.1 to 9.9.2 and commit the regenerated wrapper jar and distributionSha256Sum — the patch fixes four High build-tool vulnerabilities (SFTP host keys, snapshot dependency verification, TestKit temp dir, daemon registry). It also picks up the 9.3 Critical version-catalog code-execution fix this wrapper lacks. Run ./gradlew wrapper --gradle-version=<ver> in the wrapper (examples/android/gradle/wrapper/). Label: `[AGENT]` · Priority: P2 · https://docs.gradle.org/9.9.2/release-notes.html (synced to BUILD_PLAN)
- [ ] Decide whether to verify your identity and register this app with Google before January 2027 — if yes, create an Android Developer Console account and register the package name(s) with the SHA-256 of the GitHub Releases signing key (plus F-Droid's key if F-Droid re-signs it); if no, add an "advanced flow" install note to the README. The app is sideloaded (GitHub Releases/Obtainium/F-Droid), exactly the installs the rollout gates, and this is a personal identity/policy call. Label: `[HUMAN]` · Priority: P2 · https://developer.android.com/blog/posts/android-developer-verification-2027 (inbox only: no /build-parsed human group on this board)

## 2026-11-26

**News and updates**

- Android Auto 18.2 now swaps the top tab row for a vertical left sidebar (plus a bigger now-playing bar) when an app gets a wide enough slice of the screen; seen in Spotify, YouTube Music, Amazon Music and the dialer, which points to a platform-level change. With Maps docked beside the app it did not appear. The existing DHU smoke covers tall/portrait; wide-but-short head units are the gap for the :car Drive HUD tiles. [9to5Google](https://9to5google.com/2026/11/25/android-auto-wide-display-update/) · [autoevolution](https://www.autoevolution.com/news/android-auto-182-starts-rolling-out-with-a-big-navigation-change-for-wide-screens-278950.html)

- Cursor SDK 1.5.0 (Nov 24, then 1.5.1 and 1.5.2 by Nov 26) adds a `shell-tool-call-delta` stream update, makes `RunResult.usage` count completed model calls on cancelled or failed runs, adds `onConnectionStateChange`, and raises `InlineMcpServersBlockedError` for policy-blocked inline MCP servers. scripts/expedition/dispatch_parallel_agents.py imports cursor_sdk unpinned and only uses `Agent.prompt`, so nothing breaks, but per-agent usage is now reliable enough to record. [SDK release notes](https://cursor.com/docs/release-notes/sdk#nov-24-2026) · [PyPI](https://pypi.org/project/cursor-sdk/)

**Tasks for the planning agent**

- [ ] Add a wide/short landscape DHU profile (with and without the Maps split) to the :car smoke checks — Android Auto now reflows apps by available width, and the 3×1 Drive HUD tiles should stay legible on wide-but-short head units. Label: `[AGENT]` · Priority: P3 · https://9to5google.com/2026/11/25/android-auto-wide-display-update/
- [ ] Pin cursor-sdk>=1.5.2 in the parallel-dispatch docs and record RunResult.usage in .cursor/parallel-dispatch/*.json — Usage is now reliable even for cancelled or failed agents, which helps track cost per sprint. Label: `[AGENT]` · Priority: P3 · https://cursor.com/docs/release-notes/sdk#nov-24-2026
