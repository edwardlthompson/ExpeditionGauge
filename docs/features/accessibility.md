# Feature: Accessibility Pack

> Sprint 17 — large text, high contrast, TalkBack labels, optional TTS.

## Acceptance criteria

- ✅ High-contrast color scheme toggle (in-app)
- ✅ Large text multiplier (`LocalTextScale` 1.25×) on Material + speed readout
- ✅ TalkBack: speed gauge `contentDescription`; existing attitude/GPS labels retained
- ✅ Optional TTS readout of speed, latG, β every 5s (Settings, off by default)
- ✅ Audible mark-event / alert tones (Settings toggle)
- ✅ `FeatureFlags.accessibilityPackEnabled`

## Container map

| Layer | Path |
|-------|------|
| Preferences | `accessibility/AudibleTones.kt` (`AccessibilityPreferences`) |
| TTS | `accessibility/MetricTtsReadout.kt` |
| Theme | `ui/theme/AccessibilityTypography.kt`, `Theme.kt` |
| Settings | `SettingsScreen.kt` |
