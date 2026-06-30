# Feature: Onboarding Tour

> Sprint 17 — skippable first-run tour (full wizard deferred to Sprint 18).

## Acceptance criteria

- ✅ Five steps: permissions → mount level → first recording → live tip → playback review
- ✅ Persisted via `OnboardingPreferences` DataStore
- ✅ Skip tour completes onboarding without blocking dashboard
- ✅ `FeatureFlags.onboardingEnabled`

## Container map

| Layer | Path |
|-------|------|
| UI | `onboarding/OnboardingTour.kt` |
| Persistence | `onboarding/OnboardingPreferences.kt` |
| Gate | `ExpeditionGaugeApp.kt` |
