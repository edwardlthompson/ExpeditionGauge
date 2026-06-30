# Feature: Playback Layout + Input

> Sprint 16 — resizable playback panels and keyboard/gamepad scrubber control.

## Acceptance criteria

- ✅ Three map/gauge weight presets (Map 70%, Balanced 60%, Gauges 30%)
- ✅ Graphs dock toggle (collapsible telemetry graph panel)
- ✅ Layout persisted per active `SettingsProfile` in Room (`playbackMapWeight`, `playbackGraphsExpanded`)
- ✅ `PlaybackEngine` applies profile layout on session open
- ✅ Keyboard/gamepad: ←/→ ±1s seek, Space play/pause, `[`/`]` speed via `onKeyEvent`
- ✅ `FeatureFlags.playbackLayoutEnabled` gates layout chips (wave 2 polish)
- ✅ ADB: `playback-keyboard-seek`, `playback-layout-rotation`

## Layout presets

| Preset | Map weight | Use case |
|--------|------------|----------|
| Map | 0.7 | Route + drift overlay emphasis |
| Balanced | 0.6 | Default split |
| Gauges | 0.3 | Metrics + lap list emphasis |

Graphs toggle shows/hides `TelemetryGraphPanel` in the bottom dock without affecting map split.

## Container map

| Layer | Path |
|-------|------|
| Models | `playback/PlaybackLayoutState.kt`, `playback/PlaybackModels.kt` |
| Engine | `playback/PlaybackEngine.kt`, `playback/PlaybackInputHandler.kt` |
| Persistence | `settings/SettingsProfile.kt`, `SettingsProfileRepository.updatePlaybackLayout` |
| UI | `ui/playback/PlaybackLayoutControls.kt`, `PlaybackScreen.kt` |
| Tests | `PlaybackLayoutStateTest`, `PlaybackInputHandlerTest`, `PlaybackEngineTest`, `SettingsProfileTest` |

## Definition of Done

- Layout chips update `Row` weights immediately; profile JSON updated on change
- Rotation/configuration change retains `PlaybackEngine` state (singleton services)
- Focused playback screen consumes DPAD/media keys without losing scrubber position
