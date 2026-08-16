# Dashboard HUD v2 — acceptance criteria

Feature slice shipped in v2.10: G-trail, rotation-aware ball, portrait telemetry, hamburger chrome, Play/Stop top bar, auto-record, storage loop, protect session.

## Gauge / HUD

- [ ] G-ball trail visible in G-force and hybrid modes; fades oldest→newest; clears on calibrate and session stop
- [ ] Forward lonG moves ball **up** on screen at rotations 0–3 (unit: `GaugeDisplayRotationTest`)
- [ ] Portrait shows DMS coords (N/S/E/W), altitude, speed arc, TPMS `--` without sensors
- [ ] Lat/lon G readouts show whole numbers only
- [ ] Only **Set Level** is a `Button` on the main HUD column

## Chrome

- [ ] Top bar: menu, Play→Stop (`record_play` / `record_stop`), mark event when recording
- [ ] Drawer root: Record, Preset, Library, IMU, Live (if on), Settings — large 56dp rows
- [ ] Library submenu: Sessions, Stats, recording options (while recording)
- [ ] Preset submenu: five full-width preset rows; theme, screenshot mode, and About live in Settings
- [ ] No `RecordControls`, `ImuStatusStrip`, `HomeQuickStatsStrip`, or `PresetSwitcherChip` on main column

## Recording / storage

- [ ] Storage cap % in Settings; loop prune deletes oldest unprotected sessions; never stops active recording
- [ ] All protected + over cap → new start blocked with `storage_cap_blocked` banner
- [ ] Protect toggle persists on session (`protectedFromLoop` Room v6)
- [ ] Auto-record: bonded device connect starts; trigger disconnect stops (3s debounce); manual Play unaffected

## Verification

```bash
cd examples/android && ./gradlew :app:testDebugUnitTest
bash scripts/watch-agent-gates.sh --once --autofix
pwsh scripts/expedition/adb-smoke.ps1 -Scenario cold-start  # taps record_play / record_stop
```
