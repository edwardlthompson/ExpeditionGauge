# Feature: donations-updates

> Quiet Venmo donate, one optional note after a version change, and a silent daily GitHub installer check. Product About already lives in `dev.foss.expeditiongauge.about`.

## Acceptance criteria

- ✅ Quiet **Donate via Venmo** in About; never on the update/install dialog
- ✅ First run records the installed version with no donate popup
- ✅ After a later launch where the installed version changed: one optional note (Donate | Not now)
- ✅ Once per 24 hours, fetch GitHub `releases/latest` (User-Agent + 10s timeout); compare installer filenames
- ✅ Newer matching asset and not dismissed: **Install** | **Later**
- ✅ Failed fetch, timeout, empty assets, or same version: stay silent
- ✅ Offline/error: no network required for donate links or first-run version record
- ✅ i18n: `about_donate*`, `about_not_now`, `about_install`, `about_later`

## Smoke scenario

1. Given a fresh install, the app records the version and does not show a donate note
2. When the installed version changes on a later launch, the reminder appears once
3. Then either button hides it until the next version change; a newer installer can show Install | Later separately

## Container map

| Layer | Path |
|-------|------|
| Logic | `examples/android/.../about/ProductUpdate.kt` |
| Fetch/prefs | `GithubRelease.kt`, `UpdateLaunchPrefs.kt`, `AppUpdates.kt` |
| View | `ui/about/AboutScreen.kt`, `ui/about/LaunchPromptDialogs.kt` |
| Tests | `src/test/.../about/` |
| Wiring | `ExpeditionGaugeApp` ≤10 lines |
## Tests

- Automated: yes — `src/test/java/dev/foss/expeditiongauge/about/`
- Coverage: version compare, donate nudge, update prompt

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

- Port remaining stub patterns into the existing About folder. Do not copy `examples/` over the app.
