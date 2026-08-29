# Decision Log

> Chronological register of major technical trade-offs, accepted architectures, and rejected alternatives.
> **Treat past entries as immutable history; append only.**

## Format

```markdown
### YYYY-MM-DD — [Title]
- **Status:** Accepted | Rejected | Superseded
- **Context:** ...
- **Decision:** ...
- **Alternatives considered:** ...
- **Consequences:** ...

```

## Entries

### 2026-08-29 — Sprint 32 Record / Relive rows 44–73
- **Status:** Accepted
- **Context:** Autonomous `/build 44-73` on the template-upgrade branch. Rows 44–69 were already gated; 70–73 remained.
- **Decision:** Keep new prefs/hooks in dedicated containers (`gpxghostimport`, `ghostsectorcompare`, `ghostvideooverlay`, `trackautodetect`). Do not grow `SettingsPreferences`, `RecordingWriter`, `strings.xml` (300), or Relive `PlaybackScreen`. Ghost burn-in pairs nearest lap/ghost lines; track autodetect closes a GPS loop (≥200 m travel, ≤25 m return) into `TrackLineBuilder` GeoJSON. Named sector compare wraps existing `GhostLapComparer`.
- **Alternatives considered:** Dual-session Room ghost import (rejected — GPX/FIT text parse is enough). Grow `VideoOverlayCompositor` with ghost fields (rejected — circular draw vs pair; `GhostVideoOverlay` owns pairing). Live-only autodetect without last-session samples (rejected — Track setup should use the latest recording).
- **Consequences:** `watch-agent-gates --scope auto` passed after 71–73. Sprint 32 Maps/offline starts at row 74. `dhu-screenshot-ci` stays in `HUMAN_BACKLOG.md`.

### 2026-08-29 — Sprint 31 Golden Path catch-up
- **Status:** Accepted
- **Context:** `/build` after template `/upgrade` to v1.0.0. Seven Sequential `/feature` rows were open; product About/Settings already existed.
- **Decision:** Port stub patterns into `dev.foss.expeditiongauge` (opt-in `PendingCrash`, feedback dialogs, issue-form URLs, privacy sanitizer, same-resolution high-refresh). Do not copy template `examples/` over the app. Crash persist defaults off.
- **Alternatives considered:** Copy goldenpath packages (rejected — Sacred product tree). Keep always-on `last_crash.txt` (rejected — Golden Path is opt-in).
- **Consequences:** `watch-agent-gates --scope auto` and `feature-gate --stack android` passed. Human/device items stay in `HUMAN_BACKLOG.md`.

### 2026-08-28 — Ship v2.18.12
- **Status:** Accepted
- **Context:** `/ship` after PR #21 (DTC footer waited on the ~30 s poll tick). Pre-release feature-gate + CI/Security/CodeQL green. Local debug keystore signs the APK so OP13/OP12 can upgrade.
- **Decision:** Merge the hotfix to `main`, tag `v2.18.12`, publish a normal GitHub release with signed APK + AA-install-kit. Template `.template-version` stays 0.15.1.
- **Alternatives considered:** Let the cloud agent publish the APK (rejected — VM debug cert does not match the phones). Skip the 30 s fallback (rejected — ECU codes can still change while connected).
- **Consequences:** Sideload with Play Store spoof on OP13 and OP12. First DTCs should appear within one ELM 03/07 round-trip after Connected.

### 2026-08-28 — Immediate DTC scan on OBD handshake
- **Status:** Accepted
- **Context:** AA/phone DTC readout already worked, but codes sometimes appeared much later than the Connected handshake. Mode 03/07 lived only on the poll loop with a ~30 s fallback; a missed first tick or reconnect that did not reset `nextDtcAt` left the footer empty until a later rescan. There is no 30-minute timer.
- **Decision:** `ObdDtcScanScheduler` treats every confirmed RFCOMM+ELM handshake (including reconnects) as due-now. The poll job runs Mode 03/07 immediately after `Connected`, still outside the connect-timeout path. The ~30 s gated rescan stays as fallback.
- **Alternatives considered:** Put Mode 03 inside `withTimeout(INIT)` (rejected — init budget is already tight). Screen-visit trigger (rejected — user asked for scan without opening a DTC surface). Drop the 30 s path (rejected — ECU can still change codes while connected).
- **Consequences:** First codes should show within one ELM 03/07 round-trip after Connected. Tests cover confirm → scan, reconnect → scan, and the 30 s fallback.

### 2026-08-28 — Ship v2.18.11
- **Status:** Accepted
- **Context:** `/ship` after PR #20 (AA HDG stuck north). Pre-release feature-gate + CI/Security/CodeQL green. Dependabot REST 403 on the cloud token; GraphQL `vulnerabilityAlerts` count was 0.
- **Decision:** Merge the hotfix to `main`, tag `v2.18.11`, publish a normal (non-draft) GitHub release with signed APK + AA-install-kit. Template `.template-version` stays 0.15.1 (app semver is independent).
- **Alternatives considered:** Hold ship until Dependabot REST scope works (rejected — GraphQL shows zero open alerts). Skip the APK asset (rejected — ExpeditionGauge releases must include a signed APK).
- **Consequences:** Sideload with the AA-install-kit Play Store spoof. If this session’s debug keystore differs from prior sideloads, upgrade needs uninstall or a local re-sign.

### 2026-08-28 — AA HDG uses GNSS chip course-over-ground
- **Status:** Accepted
- **Context:** Head-unit heading stuck on due north, briefly flashing the real course. `PhoneGpsProvider.resolveCourse` preferred noisy lat/lon deltas (2 Hz × 2 m floor) then fell back to `0f` when `hasBearing()` was false. IMU publish treated that 0° as GPS COG while moving, fighting chip bearing and magnetometer yaw (Madgwick init = 0°, vehicle mag distortion).
- **Decision:** Rank sources by driving accuracy: (1) GNSS chip COG / NMEA RMC, (2) lat/lon delta only after ≥8 m on GPS_PROVIDER, (3) hold last good course. Treat chip 0° as uninitialized when motion or last COG disagrees by >25°. IMU yaw is HDG fallback only before the first valid GPS course. Network fixes never contribute course.
- **Alternatives considered:** Always use IMU/mag in the car (rejected — steel/electronics). Prefer lat/lon over chip (rejected — 3–5 m jitter at 2 Hz). Keep 0° as “unknown” (rejected — HUD shows due north).
- **Consequences:** v2.18.11. True due-north travel still works when chip and motion agree. β still suppressed below 2 m/s.

### 2026-08-26 — Publish GitHub releases (no drafts)
- **Status:** Accepted
- **Context:** v2.18.10 shipped as a draft; several older tags were draft-only. Driver asked for normal releases going forward.
- **Decision:** `project.config.json` `releaseDraft` is false. Published `v2.18.10` as Latest, rolled every draft’s notes into that release body, deleted the draft GitHub releases (git tags kept).
- **Alternatives considered:** Leave historical drafts in place (rejected).
- **Consequences:** Next `/ship` creates a published release. Notes for deleted drafts live on the v2.18.10 GitHub page and in `CHANGELOG.md`.

### 2026-08-26 — Keep seven even HUD rows
- **Status:** Accepted
- **Context:** Driver confirmed the 7-row AA telemetry cube (speed, HDG, elev, lat, lon, links, pedal) after DHU review. `/ship` v2.18.10.
- **Decision:** Keep `TelemetryCubeLayout` seven even rows. Do not restore tag `hud-pre-seven-row`.
- **Alternatives considered:** Revert to packed text + bottom-pinned bar (rejected).
- **Consequences:** GitHub release `v2.18.10` is published Latest.

### 2026-08-26 — Telemetry cube seven even rows
- **Status:** Superseded by Keep seven even HUD rows (same day)
- **Context:** Link icons sat on the pedal bar with a larger gap above them than between text lines. The cube needed even, dedicated rows.
- **Decision:** Always 7 equal bands (speed, HDG, elev, lat, lon, links, pedal). Content is centered in each band. Pedal is a few pixels thicker and inset inside the last row. Prior look is git tag `hud-pre-seven-row` (`8e7def6`).
- **Alternatives considered:** Keep packed text + bottom-pinned bar (checkpointed). Different row counts (rejected — 7 matches the live HUD fields).
- **Consequences:** v2.18.10. Restore with `git checkout hud-pre-seven-row --` on the three cube layout files if the even-row look is reverted.

### 2026-08-26 — Pedal bar layout + Expedition APP PIDs
- **Status:** Accepted
- **Context:** AA telemetry cube dropped the longitude (E/W) line when the brake/throttle bar took ~9% of the cube. Mode 01 PID `0111` on the 2006 Expedition is throttle-*plate* angle (tracks airflow / TAC), not accelerator pedal.
- **Decision:** Shrink the cube pedal bar (~2% height, needles inside the track), put it under the link icons at the cube bottom, and scale text so speed/HDG/elev/lat/lon always draw. Phone HUD pins a 6 dp bar under the link row. Throttle poll discovers `0149`/`014A`/`014B`, then Ford Mode 22 `2209D4`/`220911`/`221340` (PCM `ATSH7E0`), else `0111`.
- **Alternatives considered:** Overlay the bar on coords (rejected — hides E/W). Poll `0110` MAF (rejected — grams/sec, not pedal). Always Mode 22 (rejected — slower; generic APP exists on some PCMs).
- **Consequences:** logcat `throttlePid=` shows the live channel; first OBD poll after connect may take ~1 s for discovery.

### 2026-08-21 — Donations and installer update check
- **Status:** Accepted
- **Context:** Continuum Calendar already had a quiet Venmo link plus a once-per-version donate note and a daily GitHub installer check. ExpeditionGauge still used opt-in tag-based About updates and a placeholder donate block.
- **Decision:** Hardcode the public Venmo URL in About and Settings. After a version change, show one optional donate note (never on the update dialog). Check GitHub once per 24 hours and compare `ExpeditionGauge-X.Y.Z.apk` filenames; Install opens the asset URL; Later silences that version. Prefs stay device-local.
- **Alternatives considered:** Keep update checks opt-in/off by default (rejected — the Continuum method is automatic daily, silent on failure). Put donate on the HUD drawer (rejected — driving-safe menu).
- **Consequences:** v2.18.9; Settings can still disable the daily check; first run records the version with no donate popup.

### 2026-08-16 — Driving-safe drawer and Settings hub
- **Status:** Accepted
- **Context:** The HUD hamburger listed ~15–20 mixed controls (radios, theme icon, screenshot mode) that were hard to hit while driving; Settings was one parked-time scroll.
- **Decision:** Root drawer is Record, Preset, Library, IMU, Live, Settings (56dp rows). Preset and Library are in-drawer pages. Theme, screenshot mode, and About move to a Settings category hub. Params grouped as `SettingsUiState` / `SettingsUiActions`.
- **Alternatives considered:** Display/Sensors nests (rejected — extra tap for presets; empty Sensors). Remove Record from the drawer (rejected — muscle memory). Restore last Settings category (rejected — unpredictable when returning from the HUD).
- **Consequences:** v2.18.8; Sessions/Stats cost one Library tap; Record stays on HUD and drawer.

### 2026-08-15 — Dual brake/throttle pedal indicators
- **Status:** Accepted
- **Context:** A driver can apply brake and throttle together; the cube bar used one needle and zeroed brake whenever throttle was open.
- **Decision:** Independent 0–1 channels (PID 0111 + lonG); two needles and fills; per-side flash; AA cache packs both into `pedalQ`.
- **Alternatives considered:** Keep exclusive position (rejected — hides overlap).
- **Consequences:** Phone and AA show both pedals at once; brake still inferred from lonG.

### 2026-08-14 — CAN DTC framing + Ford HS-CAN prefer
- **Status:** Accepted
- **Context:** 2006 Expedition King Ranch scanner DTCs had zero overlap with EG; RPM on the cube was ~10× high. Gen2 DLC has J1850 PWM and HS-CAN; PCM generic OBD is CAN.
- **Decision:** Parse Mode 03/07 with CAN count-byte (ISO 3-slot fallback); ATSP0 + 0100 lock; if ATDPN is 1–5, retry ATSP6. Cube shows brake/throttle bar (0111 + lonG), not RPM.
- **Alternatives considered:** Cycle ATSP1–9 every connect (rejected — slow, can lock the wrong bus).
- **Consequences:** Scanner-matching stored codes when on HS-CAN; AA/phone pedal bar; RPM still recorded/alerted.

### 2026-08-11 — DataStore BT reconnect + pending DTCs
- **Status:** Accepted
- **Context:** GLO 2 would not stay connected; AA Drive HUD missed a DTC the driver could see on a scanner. Any Settings DataStore write re-emitted OBD/GPS address flows and called `connect()` (tearing down SPP). DTC path only read Mode 03 and skipped it when `0101` count was 0 (pending faults never shown).
- **Decision:** `distinctUntilChanged` on OBD/GPS prefs collectors; connect only if not already Connected/Connecting; GLO backoff reconnect; keep OBD/GLO across Activity destroy when AA `SensorHold` > 0; always Mode 03 + Mode 07 for the AA footer.
- **Alternatives considered:** System mock-location for GLO (rejected — steals SPP); Mode 03-only with longer 0101 gate (rejected — hides pending codes).
- **Consequences:** Settings edits no longer drop GLO/OBD; pending DTCs appear on ROW AA footer; COLUMN still has no DTC band.

### 2026-08-09 — External GPS priority + BT disconnect resilience
- **Status:** Accepted
- **Context:** Device crash log showed fatal `IOException: bt socket closed` from `ExternalNmeaGpsManager.readLoop` when Garmin GLO 2 dropped; OBD had the same uncaught socket-closed pattern. User requires external GPS over phone GNSS.
- **Decision:** Catch socket IO in external GPS read loop and OBD poll (disconnect/Failed, optional GPS reconnect); prefer external while connected or fresh valid fix (`GpsSourcePriority`); reuse ObdRfcomm secure→insecure for GLO.
- **Alternatives considered:** Let coroutine crash and rely on process restart (rejected); stop phone GPS entirely when external enabled even without fix (rejected — keep phone until link/fix is live).
- **Consequences:** Socket drops no longer kill the app; HUD uses GLO when connected; phone resumes after external stale/disconnect.

### 2026-08-04 — OBD connect critical path
- **Status:** Accepted
- **Context:** Classic Bluetooth OBD stopped connecting in EG while OBDLink still worked; post-2.18.0 DTC path and a broken ELM prompt reader made RFCOMM/init fail under the connect timeout.
- **Decision:** Wait for ELM `>` with sleep-on-empty (no 20-char ready()-only cap); keep Mode 03 off the connect timeout (first DTC tick on poll loop); try insecure SPP after secure; Settings write address only (prefs Flow owns `connect()`).
- **Alternatives considered:** Longer single timeout wrapping Mode 03 (rejected — still races catalog/load); secure-only RFCOMM (rejected — many clones need insecure).
- **Consequences:** Connect = RFCOMM + AT init; DTCs appear shortly after Connected; dual connect from Settings no longer tears down handshake.

### 2026-07-30 — Linear G + attitude settle gate
- **Status:** Accepted
- **Context:** Logcat showed parked phone `latG≈0.95` (raw accel including gravity) and “Extreme Pitch” TTS during Madgwick cold-start converge; BLE pause/resume hit Android scan-rate limits.
- **Decision:** Prefer `TYPE_LINEAR_ACCELERATION` for latG/lonG (Madgwick gravity subtract fallback); debounce shared BLE OS scan; suppress pitch/roll feedback until attitude stable ~1 s within 2.5° (plus 1.5 s floor).
- **Alternatives considered:** Accel low-pass as gravity (rejected — absorbs sustained cornering); longer fixed grace only (rejected — still fires mid-settle).
- **Consequences:** G-meter at rest ~0 G; desk TTS quiet until settle; BLE startScan quota preserved on home/resume.

### 2026-07-28 — AA OBD DTC footer (OBDex CC0)
- **Status:** Accepted
- **Context:** Drivers want stored DTCs glanceable on the AA ROW Drive HUD after OBD connect; OBDForge has a good catalog but is GPL-3.0-or-later (incompatible with MIT production path).
- **Decision:** ELM Mode 03 after each successful connect (before Mode 01 poll), then **gated ~30 s refresh** while connected: Mode 01 PID 01 (MIL + count) first, Mode 03 only when count > 0 or UI needs clear. ECUs do not push DTCs. Resolve titles from vendored slim **OBDex** CC0 asset (`assets/dtc/obdex_en.gz` — same catalog OBDForge uses, not OBDForge sources). ROW HUD paints a bold-red single-line carousel (`n/N` + code + truncated desc, 5 s dwell); COLUMN omits footer; empty/`NO DATA` hides footer; disconnect clears. Regen via `scripts/expedition/fetch-obdex-dtc.py`.
- **Alternatives considered:** Link/vendor OBDForge (rejected — GPL); Mode 03 every Mode 01 poll (rejected — bus spam); Mode 04 clear (out of scope); phone Compose footer (deferred).
- **Consequences:** ~560 KB asset in APK; AA Surface cube height reserves ~12% for optional footer; carousel invalidate on dwell boundaries.

### 2026-07-27 — AA tall Surface 1×2 (split-friendly)
- **Status:** Accepted
- **Context:** OEM split-screen gives a tall/narrow Surface where a 3×1 strip is unusable; users leave EG and lose sensor hold / alert TTS.
- **Decision:** When visible `H` clearly exceeds `W` (15% enter / leave hysteresis), render Attitude|Telemetry **1×2**, omit TPMS tile and Capture/Record/Level; keep map PAN. Fully empty ActionStrip crashes AA host (“unexpected error”) — tall mode keeps **icon-only Mute** only. Wide path unchanged. `dhu-smoke -Tall` uses `dhu-tall.ini` at **720×1280** (480×800 ignored on Windows DHU 2.0).
- **Alternatives considered:** Always 1×3 with TPMS (rejected — too tall/cramped); Session hold after Screen destroy (out of scope); MapWithContent side card (ADR-0010 rejected); zero-action strip (rejected — host crash).
- **Consequences:** Tall mode shows Mute icon only; TPMS alerts still audio while session held; leaving EG still ends sensors.

### 2026-07-26 — TPMS QR pairing wizard
- **Status:** Accepted (amended 2026-07-27: live-only MAC resolve)
- **Context:** Valve-stem BLE TPMS modules ship with printed QR codes; users need a guided FL→FR→RL→RR setup that remembers sensors across launches. Moman C4 / DJTPMS QRs encode short binding IDs (last 3 MAC bytes), not full MACs.
- **Decision:** FOSS CameraX + ZXing decode; accept full MAC **or** 4/6/8-hex sensor IDs; resolve **only** via live advertisement suffix match (eucplanet/omadon). OUI guessing (`AC:15:85` vs `3B:60:00`) removed after wrong bindings. Confirm before persist; `assignCornerExclusive`; ghost sessions; camera-denied → manual ID/MAC.
- **Alternatives considered:** ML Kit barcode (rejected — Play Services / FOSS); OUI prefix prediction (rejected — user picked wrong of two); deflate-to-identify heuristics (out of scope).
- **Consequences:** Wizard waits for live ads; device map flipped to `3B:60:00:…` suffixes when user reported `AC:15:85` guesses wrong.

### 2026-07-27 — Alert TTS AudioHardening mute (AA/DHU)
- **Status:** Accepted
- **Context:** Extreme pitch/roll fired TTS (`ExpeditionGauge/Alerts`) but no sound; logcat showed `AudioHardening background playback would be muted for com.google.android.tts`.
- **Decision:** Request `AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK` with nav-guidance attrs before `TextToSpeech.speak` (`AlertAudioFocus` + utterance-scoped abandon); same transient media focus for beeps; default `audible_tones` preference to on.
- **Alternatives considered:** Rely on USAGE alone without focus (rejected — OEM hardening mutes TTS process); play only on phone speaker stream (rejected — breaks AA path).
- **Consequences:** DHU/car should duck media and hear “Extreme Pitch/Roll”; verify with `aa-audio-smoke.ps1` + AlertTts log lines.

### 2026-07-27 — OBD speed spike + AA alert audio routing
- **Status:** Accepted
- **Context:** After OBD connect, HUD could jump to ~147 (mis-parsed ELM buffer / takeLast byte). Alert beeps used `STREAM_NOTIFICATION`, poor AA/DHU routing.
- **Decision:** Anchor PID parses on `41xx` headers (`parseVehicleSpeedKmh`); reject OBD speed when GPS is near-rest but OBD claims highway; TTS `USAGE_ASSISTANCE_NAVIGATION_GUIDANCE`; beeps `STREAM_MUSIC`; `aa-audio-smoke.ps1` checklist.
- **Alternatives considered:** Always prefer GPS (rejected — OBD useful when GPS weak); keep notification stream (rejected — weak AA projection).
- **Consequences:** Unit tests for polluted `410D` buffers and plausibility gate.

### 2026-07-26 — Sprint 28 sensor links, pairing, alert TTS, AA mute
- **Status:** Accepted
- **Context:** Stakeholders needed HUD link icons, real OBD/TPMS connection UX, continuous over-limit Beep|TTS, red/bold readouts, and one-tap AA mute while driving.
- **Decision:** Ship Sprint 28 as sequential vertical slices: `SensorLinkState` icons; OBD ELM validate + cold-start reconnect + TPMS/IMU map persistence; level-triggered 1 s alert feedback with Beep|TTS + tire phrases; Settings-persisted `alertsMuted` mirrored as AA ActionStrip Mute (first). Mute silences audio only.
- **Alternatives considered:** In-app Classic `createBond` without system BT settings (kept as helper + system settings CTA for OEM reliability); session-only AA mute (rejected — phone/AA stay in sync).
- **Consequences:** `watch-agent-gates` green; unit tests for links/alerts/codec green. DHU mute screenshot needs human Start head unit server on OEM (adb cannot launch DeveloperSettingsActivity).

### 2026-07-22 — Release Please automerge N/A for ExpeditionGauge
- **Status:** Accepted
- **Context:** Alignment left `[HUMAN]` R2 open to evaluate upstream `release-please-automerge.yml`. Local `release-please.yml` is gated to `edwardlthompson/agent-project-bootstrap` only; app ships via Gradle `versionName` + `create-release.ps1`.
- **Decision:** Do not add automerge. Close R2 as N/A; keep Android release path.
- **Alternatives considered:** Re-enable Release Please for this child and auto-merge (rejected — fights app semver / existing release tooling).
- **Consequences:** No `release-please--*` PRs on this repo; template lineage version stays in `.template-version` for upgrade tracking only.

### 2026-07-21 — Bootstrap alignment 0.11.1 → 0.15.1
- **Status:** Accepted (automerge deferral superseded by 2026-07-22 N/A)
- **Context:** Child repo already had full agent surface at template 0.11.1; upstream reached v0.15.1 (local-compute, skills, worktrees/permissions, multicore validate). Live Android product must not be rewritten.
- **Decision:** Surgical FOSS alignment only: adopt local-compute + 4 skills + worktree bundle + permissions + parallel check helper + CURSOR_CLI + FOSS plugin pack; defer `release-please-automerge.yml` pending HUMAN evaluate; skip commercial tier; never overwrite `scripts/expedition/**` or product sources; bump `.template-version` only after local gates pass. Gap/risk log: [`docs/BOOTSTRAP_ALIGNMENT.md`](docs/BOOTSTRAP_ALIGNMENT.md).
- **Alternatives considered:** Blind template sync (rejected — CI/product risk); enable Release Please automerge now (deferred then closed N/A); commercial-compliance activation (rejected — foss tier).
- **Consequences:** Agent tooling closer to upstream 0.15.1; Release Please remains template-repo-only for this child.

### 2026-07-21 — AA telemetry cube type scale + cardinals
- **Status:** Accepted
- **Context:** DHU night mode made HDG/elev/coords hard to read; spare vertical space in the middle cube; stakeholders wanted N/NE/… after the digital heading.
- **Decision:** Keep speed at `0.14×` cube; enlarge secondary/tertiary; brighten dark `secondaryText`; vertically center the text block via font metrics; append 16-point `GaugeLogic.cardinalAbbreviation` to AA `HDG` labels.
- **Alternatives considered:** Grow speed too (rejected — keep hero hierarchy); 8-point only (rejected — “variations in between”); host template fonts (not app-controlled).
- **Consequences:** v2.17.1; verified OP13 + DHU with Play Store spoof install.

### 2026-07-19 — AA NavigationTemplate Surface Drive HUD (primary)
- **Status:** Accepted (supersedes Pane-as-primary from 2026-07-18 for the live path)
- **Context:** Pane large-image slot still letterboxed; stakeholders wanted a full-bleed native 3×1 strip with tap-to-cycle attitude and titled chrome.
- **Decision:** Primary AA screen is `DriveMapHudScreen` + `NavigationTemplate` + host Surface (`DriveHudSurfacePainter`); 3×1 Attitude|Telemetry|TPMS; left-third tap cycles `AttitudeGaugeMode` (needs `Action.PAN`); PaneTemplate remains fallback; chrome Screenshot/Record/Level with `FLAG_IS_PERSISTENT`.
- **Alternatives considered:** Pane-only (kept as fallback); Grid thumbs (rejected — too small); custom Compose on AA (impossible).
- **Consequences:** v2.17.0; DHU verified on OP13; USB HU retest remains M-003; light/dark cube chrome from host `isDarkMode`.

### 2026-07-18 — AA PaneTemplate Drive HUD (large image)
- **Status:** Accepted
- **Context:** GridTemplate thumbs (~128 dp) were unusable as a driving HUD even with denser glance bitmaps; stakeholders needed larger imagery within Car App Library rules.
- **Decision:** Primary AA screen is `DrivePaneScreen` + `PaneTemplate` with `Pane.setImage` (~480 dp guidance, `MAX_PANE_BITMAP_PX=640`); composite `DriveHudBitmapRenderer`; Record/Zero as pane body actions; `minCarApiLevel` 4; revise ADR-0010.
- **Alternatives considered:** `GridTemplate.setItemSize(LARGE)` only (rejected — still multi-thumb); Tab of three Panes (deferred); Navigation/Map templates (rejected — category/ADR).
- **Consequences:** Reinstall + DHU retest (M-005); Grid helpers retained but not session entry.

### 2026-07-18 — AA HU UX densify + Cursor/DHU preview
- **Status:** Accepted
- **Context:** Projected Android Auto on the aftermarket HU showed a sparse 3-tile GridTemplate that cannot mirror the phone Compose HUD; stakeholders wanted denser glanceables and a way to preview in Cursor without living in Android Studio.
- **Decision:** Keep ADR-0010 `GridTemplate`; put glanceable numbers on Telemetry/TPMS `CarIcon` bitmaps; prioritize secondary text (speed·HDG; TPMS pressures); add `dhu-preview.ps1` + `aa-bitmap-preview.ps1` (Robolectric `@GraphicsMode(NATIVE)` PNGs); document native APK Route A (M-004) for phone-like HUD.
- **Alternatives considered:** Phone-identical AA Compose (impossible — host templates); shrink host fonts (not app-controlled); text-only List/Pane as primary (rejected — worse attitude UX); AS-only refresh loop (rejected — DHU CLI is enough).
- **Consequences:** BUILD_PLAN AA HU UX sprint; M-003/M-004 remain `[ADB]`; denser tiles on next APK install to HU.

### 2026-07-17 — Ship AA-install-kit.zip on every GitHub Release
- **Status:** Accepted
- **Context:** README promised `ExpeditionGauge-*-AA-install-kit.zip` but `create-release.ps1` only uploaded the APK; after plain sideloads Customize launcher stayed empty (`initiatingPackageName=com.android.shell`).
- **Decision:** Add `pack-aa-install-kit.ps1` + `aa-spoof-adb.sh`; attach the kit from `create-release.ps1`; document a copy-paste ADB spoof in README; keep Magisk `su` with `run-as-uid-arm64` fallback for adb-root devices.
- **Alternatives considered:** Document-only (rejected — users still lacked the zip); rename APK as “spoofed” (rejected — bytes are unchanged; attribution is install-time).
- **Consequences:** v2.16.3+ releases include kit; upgrades must re-run spoof install or AA hides the app.

### 2026-07-15 — AA smoothness hardening (bridge, bitmap, sensors, GridTemplate)
- **Status:** Accepted
- **Context:** Deep AA audit after ActionStrip crash: `StorageCapBlockedException` escaped host clicks (process FATAL); shared mutable inclinometer bitmap raced phone Offroad vs AA; Activity `onStop` froze AA sensors; GridItem `\n` text is single-line truncated; ConstraintManager polled every template; invalidate at 250 ms overworked hosts.
- **Decision:** Async `runCatching` bridge mutators + `CarToast`; size-keyed renderer pool + `Bitmap.copy`; AA max 256 px + CarIcon cache (0.1°); single-line tile text; session-locked `AaDisplaySpec`; sensor refcount for Activity+AA; Record icon + parked-only Zero; invalidate floor 500 ms; delete dead `CarTelemetryHost`.
- **Alternatives considered:** `runBlocking` with try/catch only (rejected — still blocks Binder/main); share renderer without copy (rejected — bleed); ParkedOnly on Record (rejected — allow record while driving).
- **Consequences:** Debug APK path ready for HU retest; ship as **v2.16.2**; ADR-0010 items 11–13.

### 2026-07-15 — AA GridTemplate ActionStrip max one custom title
- **Status:** Accepted
- **Context:** OP13 logcat: launching ExpeditionGauge on Android Auto crashed immediately with `IllegalArgumentException: Action list exceeded max number of 1 actions with custom titles` at `TelemetryGridScreen.onGetTemplate` (`GridTemplate.setActionStrip`). v2.16.1 still used titled **Record/Stop** and titled **Zero**.
- **Decision:** Keep titled Record/Stop (single custom-title slot); make Zero **icon-only** (`ic_aa_zero`); extract `TelemetryGridActions` + Robolectric regression that `GridTemplate` accepts the strip and rejects two titled actions.
- **Alternatives considered:** Move Zero onto Attitude tile click (rejected — accidental calibrate); PaneTemplate / second screen (rejected — ADR-0010 strip UX).
- **Consequences:** Debug APK installed on OP13 for HU retest; patch release (e.g. 2.16.2) when human ships; docs note icon-only Zero.

### 2026-07-13 — AA GridItem crash fix + HU-independent display spec + local crash log
- **Status:** Accepted
- **Context:** OP13 + truck HU: launching ExpeditionGauge on Android Auto killed phone and HU with `IllegalStateException: When a grid item is loading, the image must not be set and vice versa` (Telemetry/TPMS GridItems had no image). Phone portrait + landscape HU must keep working.
- **Decision:** Always `setImage` on GridItems (resource icons + `CarIcon.APP_ICON` fallback); `AaDisplaySpec` from CarContext only (never phone Display.rotation); local `CrashLogStore` + Settings share; AA Zero stays vehicle-frame `displayRotation=0`.
- **Alternatives considered:** `setLoading(true)` for text tiles (rejected — spinner UX); PaneTemplate (rejected — ADR-0010); remote crash SDKs (rejected — FOSS/privacy).
- **Consequences:** v2.16.1; Settings → Android Auto → Last crash; mixed phone/HU orientations supported by construction.

_Seed template ADR: `docs/adr/0000-template-baseline.md`. Child repos use `docs/adr/0001-core-architecture.md`._

### 2026-07-12 — Gauge cycle, G-meter axes, USGS DEM elevation
- **Status:** Accepted
- **Context:** Users need each inclinometer style as a standalone gauge; Attitude/Hybrid redundant with G-meter; weak GNSS altitude was tens of meters off vs USGS terrain
- **Decision:** Six-mode cycle (ladder/horizon/dual/bubble/G-meter/compass); single pitch/roll G-meter ball with identity portrait cube; DEM fallback via USGS EPQS when sats/vAcc are poor; A-GPS inject for fix quality only
- **Alternatives considered:** Accel-G / hybrid ball modes (rejected — user preference); ASTER-only DEM (rejected — 72 ft vs USGS 7.8 ft at Guaynabo); barometer fusion (deferred — needs sea-level reference)
- **Consequences:** v2.16.0; elevation needs network once per cell; legacy attitude/hybrid prefs map to G-meter

### 2026-07-12 — Integer P/R/Y + stationary pose autocalibrate
- **Status:** Accepted
- **Context:** Decimal attitude jitter; mount pose needs a still-window Zero with honest yaw/compass behavior
- **Decision:** Whole-degree P/R/Y labels; confirm dialog after ~3s still; soft MagHeading + variance gate (P/R-only when distorted); shared commit gate with manual Zero
- **Alternatives considered:** Silent auto-Zero without confirm (rejected — slope/false forward); full Madgwick 9-DOF (deferred — MagHeading overlay sufficient for v2.15)
- **Consequences:** v2.15.0 Settings toggle default on; AA Zero shares CalibrationStore; GPS course never offset

### 2026-07-12 — Head-unit routes + soft features for HU/AAOS
- **Status:** Accepted
- **Context:** Stock AA Play-attribution blocks many FOSS users; aftermarket Android HUs, AAOS, and MITM adapters are viable dash paths
- **Decision:** Document routes in `HEAD_UNIT_ROUTES.md` / README; ship one APK with soft `uses-feature`, `distractionOptimized` MainActivity, and existing Car App path for MITM/AA
- **Alternatives considered:** Separate AAOS product flavor (deferred); claim native OpenAuto UI rewrite (out of scope — use projected AA or Android-on-SBC)
- **Consequences:** v2.14.2+ installs on HUs without phone IMU; AAOS UX remains best-effort under OEM distraction policy

### 2026-07-11 — AA sideload alternatives beyond root / public Play
- **Status:** Accepted (documentation)
- **Context:** Users need non-rooted GitHub sideload on Android 14+; research of Fermata/KingInstaller/AAAD/AAXLU/aa-proxy communities
- **Decision:** Document only proven paths: Magisk PC kit; KingInstaller ≤13; wireless MITM (AAWireless / aa-proxy-rs); private Play Internal testing. Explicitly reject Shizuku-alone, AAAD-for-arbitrary-APK (catalog only), and category churn
- **Alternatives considered:** Ship a FOSS KingInstaller fork (rejected for A14+ — spoof closed); claim USB-only unrooted A14 fix (rejected — none known); list ExpeditionGauge in AAAD (out of scope / paid catalog gate)
- **Consequences:** README and `ANDROID_AUTO_SIDELOAD.md` state hard platform limits; kit zip remains the supported rooted path

### 2026-07-11 — AA Customize launcher requires Play install initiator (not just installer)
- **Status:** Accepted
- **Context:** Device smoke showed Customize launcher listing Car Scanner (`initiatingPackageName=com.android.vending`) but not ExpeditionGauge (`installer=vending` yet `initiator=com.android.shell`). UI dump after fixing initiator: **LISTED=True** with checkbox enabled.
- **Decision:** `aa-refresh-host.ps1` creates `pm install-create` as Play Store UID, then shell `install-write`/`commit`; document KingInstaller for phone-only; keep `category.POI`
- **Alternatives considered:** packages.xml edit (blocked by fsverity); phenotype FlagOverrides (schema changed / unnecessary once initiator correct); more category swaps (rejected)
- **Consequences:** Root required for the ADB session trick; browser sideload alone will not populate Customize launcher on locked-down AA hosts

### 2026-07-11 — AA Customize launcher requires Play installer attribution
- **Status:** Accepted
- **Context:** After POI + Unknown sources, Customize launcher still empty on OnePlus; dumpsys showed `installerPackageName=null` / `initiatingPackageName=com.android.shell` from plain `adb install`
- **Decision:** Default `aa-refresh-host.ps1 -Apk` to `pm install -i com.android.vending`; document KingInstaller for phone-only; not a category change
- **Alternatives considered:** More category swaps (rejected — escalation stop-rule); require KingInstaller only (rejected — ADB path is FOSS and scriptable)
- **Consequences:** GitHub download + open APK is insufficient for AA listing on locked-down hosts; release docs must point at `aa-refresh-host.ps1`

### 2026-07-11 — Android Auto projected discovery uses POI (not IOT)
- **Status:** Accepted
- **Context:** Sideloaded ExpeditionGauge with Unknown sources still missing from head-unit Apps; device dumpsys showed `category.IOT` on 2.13.0; community FOSS apps report real cars filter IOT while DHU does not
- **Decision:** Single `androidx.car.app.category.POI` on `CarAppService`; document Customize launcher + `aa-refresh-host.ps1`; escalate OEM/cable only if Customize stays empty — no dual categories / NAVIGATION
- **Alternatives considered:** Keep IOT (rejected — filtered on projected hosts); dual IOT+POI (rejected — host confusion); NAVIGATION (rejected — map obligations)
- **Consequences:** Not Play-certification-ready; FOSS sideload path only; ship 2.14.1 with signed APK + mandatory host refresh after upgrade

### 2026-07-10 — GitHub release must attach signed APK
- **Status:** Accepted
- **Context:** v2.14.0 was published via `gh release create` with notes only; Release workflow uploaded SBOMs but no APK, so sideload/Android Auto install from Releases failed
- **Decision:** `create-release.ps1` always `assembleRelease` + `sign-release-apk.ps1` and passes `ExpeditionGauge-{version}.apk` to `gh release create`; `/push` Step 4 documents backfill via `gh release upload --clobber`
- **Alternatives considered:** CI-only APK upload in `release.yml` (deferred — signing uses local debug keystore for FOSS sideload); unsigned APK on Releases (rejected — not installable)
- **Consequences:** Agents must use `create-release.ps1` (or explicit sign+upload) for ExpeditionGauge releases; v2.14.0 backfilled with signed APK

### 2026-06-20 — Repo-wide checklist status markers
- **Status:** Accepted
- **Context:** BUILD_PLAN and scattered checklists used mixed ⬜ / `- [ ]` / ✅ formats; inconsistent in Markdown Preview vs source
- **Decision:** Standardize on 🔲 open · ✅ done · ❌ blocked emoji markers repo-wide; document in `BUILD_PLAN.md` legend and agent read order
- **Alternatives considered:** GitHub `- [ ]` task lists (rejected: poor Preview readability and agent parsing); keep ⬜ white square (rejected: visually similar to ✅ in some fonts)
- **Consequences:** All new checklist rows use emoji; `agent-progress.sh` accepts legacy ⬜ for child repos during transition

### 2026-06-18 — Release automation hardening (M29)
- **Status:** Accepted
- **Context:** v0.11.0 release lacked SBOM assets (GITHUB_TOKEN cannot chain `release` → `release.yml`); Release Please skipped `extra-files`; `health-check.yml` registered as path name caused 0-job push failures
- **Decision:** `release-please.yml` runs `sync-template-version.sh` on release PR branches and dispatches `release.yml` on `release_created`; rename workflow to `weekly-health-check.yml`; fix sync script for Windows Git Bash
- **Alternatives considered:** PAT with workflow scope for release chaining (rejected: secrets management); manual SBOM backfill only (rejected: repeated human step each release)
- **Consequences:** Release Please needs `actions: write`; future releases should ship SBOM assets without manual dispatch

### 2026-06-17 — Batch instruction templates (M27)
- **Status:** Accepted
- **Context:** Agents and child-repo owners needed repeatable shortcuts for bootstrap, verify, build, ship, and maintenance workflows without re-pasting long prompts
- **Decision:** Ship 25 slash commands in `.cursor/commands/` (20 atomic + 5 super), bare-word expansion via `batch-commands.mdc`, human cheat sheet at `docs/help/BATCH_COMMANDS.md`, registry at `docs/BATCH_COMMANDS.md`; `/push` and `/ship` grant explicit push approval
- **Alternatives considered:** `beforeSubmitPrompt` hook for bare words (rejected: Cursor API cannot rewrite prompts); single mega-doc for humans and agents (rejected: overwhelms first-time users)
- **Consequences:** `alwaysApply` rule adds ~25 lines per session; `check-batch-commands.sh` prevents registry drift; child repos cherry-pick via `UPGRADING_FROM_TEMPLATE.md`

### 2026-06-13 — @lhci/cli npm overrides for transitive CVEs
- **Status:** Accepted
- **Context:** Lighthouse CI (`@lhci/cli`) bundles transitive dependencies (`tmp`, `uuid`) with known CVEs; no patched `@lhci/cli` release available at triage time
- **Decision:** Add npm `overrides` in `examples/web/package.json` forcing `tmp >= 0.2.6` and `uuid >= 11.1.1`; document in KB-007
- **Alternatives considered:** Dismiss Dependabot alert (rejected: hides real risk); remove Lighthouse CI job (rejected: loses performance gate)
- **Consequences:** Lockfile must be regenerated after override changes; overrides should be removed when `@lhci/cli` ships fixed dependencies

### 2026-06-13 — Ship all optional ecosystem modules (M3)
- **Status:** Accepted
- **Context:** Sprint M3 asked whether to ship Lightroom, Rust, and Go optional modules in the template maintainer repo
- **Decision:** Ship all three with Golden Path stubs, MODULE.md guides, and path-gated CI jobs (`lightroom`, `rust`, `go`) that skip when child repos remove the directories
- **Alternatives considered:** Lightroom-only (rejected: Rust/Go stubs are low-cost and popular); defer all optional modules (rejected: COMPLETED_TASKS M3 work already landed)
- **Consequences:** Template CI runs more jobs on `main`; child repos can delete unused `examples/` folders to skip jobs via `hashFiles` guards

### 2026-06-30 — Sprint 17b v1.2.0 release validation
- **Status:** Accepted
- **Context:** v1.2 release gate required reproducible APK, full ADB regression matrix, and polish wave 1–3 gates; playback ADB scenarios failed after Sprint 17 `RichSessionCard` UI change
- **Decision:** Ship v1.2.0 (`versionCode` 3) with verified reproducible hash via `clean assembleRelease --no-build-cache` + `SOURCE_DATE_EPOCH=1700000000`; fix `Open-PlaybackScreen` to tap **Play** and use `Test-PlaybackScreenOpen` markers; defer `create-release.ps1 -Tag v1.2.0` until `gh` auth + human approval
- **Alternatives considered:** Revert RichSessionCard tap target to date-only card (rejected: Play is the explicit affordance); skip playback ADB rows (rejected: regression matrix is release gate)
- **Consequences:** Windows reproducible builds require clean + `--no-build-cache`; GitHub tag/release remains manual follow-up

### 2026-06-30 — Sprint 18 v2.0.0 video + export
- **Status:** Accepted
- **Context:** v2 stretch (ADR-0005) required video sync, burn-in export, calibration wizard, developer mode, and enhanced bundles without breaking phone-only core path
- **Decision:** Ship v2.0.0 with ExoPlayer sync, MediaCodec burn-in, Room v3 video fields, `EnhancedExportService` ZIP bundles; gate via `check-v2-video-gate.sh`; defer GitHub release tag until human approval
- **Alternatives considered:** Keep VideoSyncEngine stub (rejected: BUILD_PLAN row 2–6 require functional slice); cloud transcode (rejected: privacy/offline-first)
- **Consequences:** DB migration uses destructive fallback on schema bump; Media3 added to dependency lockfile

### 2026-06-30 — Initial GitHub push + BUILD_PLAN trim (v2.1.0)
- **Status:** Accepted
- **Context:** Local repo had no `origin`; BUILD_PLAN exceeded 2300 lines; CI file-limit and design-cohesion gates failed on playback splits
- **Decision:** Trim BUILD_PLAN to active board + `COMPLETED_TASKS.md` archive; split 13 oversized files; add `playback` design tokens; index v2 gate scripts in `TEMPLATE_INDEX.json`; push to `edwardlthompson/ExpeditionGauge`
- **Alternatives considered:** Keep monolithic BUILD_PLAN (rejected: agent token cost); disable file-limit gate (rejected: CI policy)
- **Consequences:** Feature specs live in `docs/features/`; next sprint is 19b system UI insets

### 2026-06-30 — Sprint 19 v2.1.0 live telemetry
- **Status:** Accepted
- **Context:** Track-day pit crews need opt-in live metrics with FOSS-only stack and no cloud broker
- **Decision:** Ship v2.1.0 with OkHttp WebSocket signaling + stub metric relay through same server until FOSS WebRTC audited; QR pairing, in-app + web receivers, DataStore settings persistence; gate via `check-v2-live-gate.sh`
- **Alternatives considered:** `stream-webrtc-android` now (deferred: large binary, audit pending); raw WebSocket-only forever (rejected: no NAT traversal on cellular)
- **Consequences:** Interim transport relays metrics via signaling server (documented in ADR-0006 / live-telemetry.md); cellular/hotspot E2E remains manual two-device validation

### 2026-06-30 — Template main parity (post-v0.11.1, SHA 1cd2b72)
- **Status:** Accepted
- **Context:** Upstream `agent-project-bootstrap` `main` gained M30 Cursor hooks/skills/subagents, autonomous `/build` (`build_sprint.py`), and sprint automation scripts unreleased after tag v0.11.1
- **Decision:** Cherry-pick Tier-1 template infrastructure; hybrid-merge `/build` and `human_task_automation.py` with Expedition `scripts/expedition/*` ADB/AUTO delegates; retain expedition `FORBIDDEN_PATHS` in `parallel_scope.py`; keep `.template-version` at 0.11.1 until upstream v0.12.0; record SHA in `.template-sync-sha`
- **Alternatives considered:** Wait for v0.12.0 tag (rejected: user requested recent main updates); blind overwrite BUILD_PLAN/examples/android (rejected: product-specific)
- **Consequences:** `validate-bootstrap.sh` runs cursor hook/integration gates; orphan `parallel.md` removed; active sprints 23–27 marked `parallel_exception: sequential-only`

### 2026-06-30 — v2.9.0 Relive wave release
- **Status:** Accepted
- **Context:** Sprints 19b–27 completed on device (OnePlus 12); Relive-style export/share features ready for GitHub Release
- **Decision:** Ship `versionCode` 10 / `versionName` 2.9.0 with draft GitHub release; archive BUILD_PLAN Relive wave to `COMPLETED_TASKS.md`
- **Alternatives considered:** Staggered 2.2–2.9 point releases (rejected: single integrated Relive wave commit matches sprint archive)
- **Consequences:** `project.config.json` enables all v2 sprint toggles; F-Droid changelog `10.txt` added

### 2026-06-30 — Post-audit hardening v2.9.1
- **Status:** Accepted
- **Context:** `/audit` deferred F-005 (privacy/backup), F-007 (Trivy Netty), F-008 (export tests + adb-smoke split); BUILD_PLAN grew to ~280 lines with duplicate archive headers
- **Decision:** Ship `versionCode` 11 / `versionName` 2.9.1 with `allowBackup=false`, ExpeditionGauge `PRIVACY.md`, `.trivyignore` for AGP test-harness Netty only, export unit tests, adb-smoke modular split, BUILD_PLAN trim to ~120 lines
- **Alternatives considered:** AGP bump to resolve Netty transitives (deferred — lockfile churn); keep template README (rejected — child repo should describe the app)
- **Consequences:** Security Scan should pass; README reflects ExpeditionGauge; KB-014 documents Trivy ignore rationale

### 2026-06-30 — Dependabot triage: defer bulk Android bump
- **Status:** Accepted
- **Context:** Dependabot PR #4 proposed Kotlin 2.4, OkHttp 5.x, Gradle 9.6, and 21 dependency bumps; CI failed on `processDebugNavigationResources` and duplicate classes
- **Decision:** Close bulk PR; keep pinned FOSS stack on main. Apply github-actions group bump on main. Release Please disabled for ExpeditionGauge child repo (`if: github.repository == 'edwardlthompson/agent-project-bootstrap'`)
- **Alternatives considered:** Merge partial bumps only (deferred — needs dedicated regression pass); keep Release Please for template semver in child repo (rejected — conflicts with app v2.9.x releases)
- **Consequences:** Zero open Critical/High Dependabot alerts after Trivy ignore + alert enablement; android-dependencies group revisit in dedicated BUILD_PLAN row when AGP/Kotlin upgrade is scoped

### 2026-06-30 — Dashboard HUD v2.10.0
- **Status:** Accepted
- **Context:** User-requested HUD cleanup: G-trail, rotation-aware axes, portrait telemetry, minimal chrome, storage loop, BT auto-record
- **Decision:** Ship `versionCode` 12 / `versionName` 2.10.0 with `GaugeDisplayRotation`, drawer + top-bar Play/Stop, Room v6 protect/trigger columns, `SessionStorageBudget`, `AutoRecordMonitor`
- **Alternatives considered:** Static portrait axis swap (rejected — mount orientation varies); stop recording when storage full (rejected — dashcam loop deletes oldest unprotected)
- **Consequences:** ADB smokes use `record_play`/`record_stop`; destructive Room migration on dev upgrades; acceptance doc `docs/features/dashboard-hud-v2.md`

### 2026-06-30 — HUD readability v2.10.1
- **Status:** Accepted
- **Context:** OnePlus 12 field feedback after v2.10.0: imperial speed not wired, canvas speed arc cluttered, elevation below MSL, menus too bright, TPMS layout unclear
- **Decision:** Ship `versionCode` 13 / `versionName` 2.10.1 with digital-only speed, `speedUnit` through HUD, `AltitudeNormalizer` (API 34+ MSL + EGM96 fallback), `GaugeMenuSurface` dark chrome, TPMS 2×2 grid, whole-number pitch/roll
- **Alternatives considered:** Ship cube layout in same release (rejected — combined roadmap splits Phase A/B); router-level BackHandler (rejected — exceeds 300-line static gate)
- **Consequences:** 193 unit tests pass; nav-bar inset deferred to v2.11.0 cube work; combined HUD roadmap Phase B next

### 2026-06-30 — HUD cube layout v2.11.0
- **Status:** Accepted
- **Context:** Post–v2.10.1 feedback: cube 1:1 tiles, portrait pitch/roll axis bug, trail only in G modes, TPMS alignment, hardcoded km/h in alerts
- **Decision:** Ship `versionCode` 14 / `versionName` 2.11.0 with `ui/dashboard/hud/` cube framework, edge numerals, `UnitDisplay`, `TpmsPressureBands`, menu nav inset
- **Alternatives considered:** Keep row layout with borders only (rejected — inconsistent small-screen behavior); router-level calibrate button (rejected — detail sheet sufficient)
- **Consequences:** TWO_TILE fallback below 480dp portrait; portrait-only `isPortraitLayout` axis remap; settings/playback/live use unit-aware labels

### 2026-06-30 — Telemetry cube density v2.11.7
- **Status:** Accepted
- **Context:** OnePlus 12 feedback after v2.11.6: telemetry cube had dead vertical space; speed/HDG and coords felt small
- **Decision:** Ship `versionCode` 21 / `versionName` 2.11.7 with enlarged digits, `TelemetryHudMetaRow` (alt/time/GPS meta), attitude and OBD extras rows, `SpaceEvenly` cube layout
- **Alternatives considered:** Show lat G in cube (rejected — user removed in v2.11.5); hide alt/time during crawling recording in cube (rejected — wastes tile space)
- **Consequences:** `SpeedHeadingRow.enlarged`, `GpsReadoutPanel.hudCube`; GAUGE_REFERENCE center section updated

### 2026-06-30 — G-meter HUD rotation v2.11.9
- **Status:** Accepted
- **Context:** OnePlus 12 tuning: portrait cube validated at ROTATION_0; rotating to landscape or upside-down broke axes when cube remap ran after `rotateBall` or roll/pitch were confused
- **Decision:** Ship `versionCode` 23 / `versionName` 2.11.9 with `mapDeviceBallToHudScreen` pipeline (portrait cube in device space → `rotateBall` → landscape post-remap); `GMETER_HUD_ROTATION.md` as canonical contract
- **Alternatives considered:** Negate device roll X for lateral flip (rejected — flips screen Y after 90° CW); skip portrait 90° CW (rejected — validated portrait behavior)
- **Consequences:** Portrait locked: mirror pitch + CW before rotate; landscape CCW@90 / CW@270 / negate@180; tests in `GaugeDisplayRotationAllOrientationsTest`

### 2026-06-30 — EG monogram launcher icon v2.11.10
- **Status:** Accepted
- **Context:** Store/launcher used default Android icon; user selected Option C EG monogram for brand identity
- **Decision:** Canonical `docs/assets/app-icon-512.png`; `sync-app-icon.py` generates mipmaps + store assets; release APK with R8 minify + shrink (~26% smaller than debug)
- **Alternatives considered:** Option A G-meter bullseye (rejected — less distinctive); ship debug APK (rejected — user requested compression)
- **Consequences:** `AndroidManifest` `android:icon`; README centered icon; GitHub releases ship `assembleRelease` APK

### 2026-06-30 — Keep screen awake v2.11.13
- **Status:** Accepted
- **Context:** In-car HUD testing: system display timeout dimmed screen during drives
- **Decision:** `FLAG_KEEP_SCREEN_ON` via `ExpeditionGaugeTheme` SideEffect; `BrightnessPreferences.keepScreenAwake` default true; Settings toggle
- **Alternatives considered:** Partial wake lock (rejected — permission + battery); dashboard-only `Modifier.keepScreenOn()` (rejected — user chose full foreground)
- **Consequences:** Clears on background; no `WAKE_LOCK` permission; documented in GAUGE_REFERENCE + PRIVACY

### 2026-06-30 — v2.13.0 AA inclinometer + quiet agent shell
- **Status:** Accepted
- **Context:** Offroad/camper leveling needs ±45° inclinometer on AA and phone; agent shell commands opened `.sh` hook tabs and stole editor focus
- **Decision:** Shared `:car` `InclinometerBitmapRenderer`; `AttitudeGaugeMode.INCLINOMETER` + Offroad preset; AA Zero action; Python hooks + `agent-run.py` (KB-017)
- **Alternatives considered:** G-force on inclinometer tile (rejected); bash hook wrappers (rejected — focus steal); Mark on AA ActionStrip (removed — phone-only)
- **Consequences:** ADR-0010 revised; `AA_INCLINOMETER.md`; M-003 ADB/DHU checklist remains open post-ship

### 2026-06-30 — v2.12.0 AA grid, imperial, route colors, offline maps
- **Status:** Accepted
- **Context:** Drivers wanted head-unit HUD without opt-in toggle; imperial display gaps in playback; drift β route colors confused driving semantics; playback map online-only
- **Decision:** Always-on `GridTemplate` 3-tile AA (`TelemetryGridScreen`); `DrivingRouteStyling` lonAccel buckets; `MapTilePrefetchWorker` + home region + cellular prompt; imperial via `UnitDisplay` on graphs/overlays/elevation
- **Alternatives considered:** Pixel-perfect Compose HUD on AA (rejected — Car App Library policy); retain AA Settings toggle (rejected — user confirmed always-on); drift β as default route color (rejected)
- **Consequences:** ADR-0010/0011; `docs/help/ANDROID_AUTO.md`; demo MapLibre tiles for offline spike; ADB validation row M-003 remains open

### 2026-07-09 — Screen-stable IMU remap (inclinometer landscape)
- **Status:** Accepted
- **Context:** Portrait Zero + 90° CCW left the aviation horizon vertical; Application WM reported ROTATION_0 while Activity was ROTATION_90; Euler unwrap after Madgwick was gimbal-fragile
- **Decision:** `SensorAxisRemap` before Madgwick; Activity `Display.rotation` authoritative; Madgwick reset on rotation change; keep locked portrait pitch↔roll swap; one vehicle-frame Zero for all orientations (ADR-0013)
- **Alternatives considered:** Post-fusion Euler unwrap (rejected — singularity); re-zero per orientation (rejected — product requirement); Application WM on every gyro sample (rejected — OEM lie)
- **Consequences:** Landscape horizon stays level after portrait Zero; `.cursor/rules/inclinometer-rotation.mdc`; `SensorAxisRemapTest` guards the matrix
