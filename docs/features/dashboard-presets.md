# Feature: Dashboard Presets + Settings Profiles

> Sprint 15 — declarative HUD layouts with persisted profiles.

## Acceptance criteria

- ✅ Five presets: Default, Drift, Offroad, Track, Minimal
- ✅ `SettingsProfile` persisted in Room + active profile id in DataStore
- ✅ Quick-switch chips on dashboard (pre-record and during recording)
- ✅ Offroad preset links to `RecordingMode.CRAWLING`
- ✅ `FeatureFlags.dashboardPresetsEnabled` gates preset UI; fusion path unchanged
- ✅ ADR-0004 accepted

## Preset summary

| Preset | Emphasis | Recording mode |
|--------|----------|----------------|
| Default | Balanced 3-panel | NORMAL |
| Drift | β readout, center weight | NORMAL |
| Offroad | Large attitude panel | CRAWLING |
| Track | Speed + lap context | NORMAL |
| Minimal | Speed + record only | NORMAL |

## Container map

| Layer | Path |
|-------|------|
| Models | `presets/DashboardPreset.kt`, `settings/SettingsProfile.kt` |
| Persistence | `SettingsProfileRepository`, `SettingsProfileEntity` |
| UI | `PresetSwitcherChip`, `SettingsPresetOptions`, `DashboardHudLayout` |
| ADR | `docs/adr/0004-dashboard-presets.md` |
| Tests | `DashboardPresetTest.kt`, `SettingsProfileTest.kt` |

## Definition of Done

- Preset switch updates layout weights without recreating `DashboardViewModel`
- Optional panels (TPMS, lap strip) respect `FeatureFlags`
- ADB `preset-switch-mid-drive` passes on physical device
