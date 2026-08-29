# Feature: onboarding-v218

> Add offline-maps and privacy-backup steps to the skippable tour.

## Acceptance criteria

- ✅ Tour includes OfflineMaps and PrivacyBackup after PlaybackReview
- ✅ ExpeditionGaugeApp wiring is unchanged
- ✅ i18n: `onboarding_offline_maps`, `onboarding_privacy_backup`

## Smoke scenario

1. Given a fresh tour
2. When the user advances past playback
3. Then offline tiles and privacy backup are explained

## Container map

| Layer | Path |
|-------|------|
| Logic | `onboardingv218/OnboardingV218.kt` |
| Tests | `app/src/test/.../onboardingv218/` |
| Wiring | `OnboardingStep` + `OnboardingTour` |

## Tests

- Automated: yes — `OnboardingV218Test`
- Coverage: extra steps; labels

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
