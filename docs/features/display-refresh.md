# Feature: display-refresh

Window requests the fastest same-resolution display mode. Scroll surfaces vote HIGH so adaptive-refresh panels can ramp during flings.

## Acceptance criteria

- ✅ About and Settings scroll at the panel's peak same-resolution rate when the OS allows it
- ✅ Missing display or empty mode list leaves `preferredDisplayModeId` unchanged
- ✅ No new controls; motion follows OS refresh (battery saver / ARR)
- ✅ i18n: N/A — no user-facing strings

## Smoke scenario

1. Given the app is running on a high-refresh device
2. When About or Settings is opened and flung
3. Then the window's preferred mode matches the fastest same-size `Display.Mode` and scroll uses a HIGH frame-rate vote

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../display/DisplayModeSelector.kt` |
| Adapter | `WindowRefresh.kt`, `HighRefreshScroll.kt` |
| Tests | `src/test/.../display/DisplayModeSelectorTest.kt` |
| Wiring | `MainActivity` one call; About/Settings scroll modifiers |
## Tests

- Automated: yes — `DisplayModeSelectorTest.kt`
- Coverage: pick fastest same-size mode; empty list is a no-op

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Package `dev.foss.expeditiongauge.display`, not `goldenpath`.
