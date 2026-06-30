# Feature: Crawling Mode

> Sprint 9 — low-speed off-road recording profile emphasizing attitude.

## Acceptance criteria

- ✅ `RecordingMode.CRAWLING` available alongside `NORMAL`
- ✅ `CrawlingModeProfile` adjusts IMU sample rate and GPS smoothing
- ✅ Phone-only rate capped at 15 Hz; external IMU allows 20 Hz
- ✅ HUD shows yellow "CRAWL" badge when active
- ✅ Attitude G-meter emphasized; map de-emphasized during record

## Smoke scenario

1. Given crawl mode enabled in settings
2. When user starts recording in CRAWLING mode
3. Then sample rate profile applies and crawl badge visible

## Container map

| Layer | Path |
|-------|------|
| Logic | `recording/CrawlingModeProfile.kt`, `recording/RecordingMode.kt`, `recording/RecordingWriter.kt` |
| UI | `ui/dashboard/DashboardScreen.kt` (CRAWL badge), `ui/settings/SettingsScreen.kt` (mode selector) |
| Tests | `recording/CrawlingModeProfileTest.kt` |

## Definition of Done

- Profile selectable per session; thermal cap documented in `docs/THERMAL_PERFORMANCE.md`
- FeatureFlags.crawlingModeEnabled gates mode selector
