# Feature: fdroid-antifeatures

> Declare F-Droid Anti-Features (none) and keep Fastlane listing in sync.

## Acceptance criteria

- ✅ Listing file says `None`
- ✅ Tracking / Ads / NonFreeNet do not apply
- ✅ Fastlane mirror has the same file
- ✅ i18n: store listing English only

## Smoke scenario

1. Given `metadata/en-US/AntiFeatures.txt`
2. When the verify script runs
3. Then Anti-Features is present and Fastlane has a copy

## Container map

| Layer | Path |
|-------|------|
| Logic | `fdroidantifeatures/FdroidAntiFeatures.kt` |
| Tests | `app/src/test/.../fdroidantifeatures/` |
| Wiring | `scripts/verify-fdroid-metadata.sh` |

## Tests

- Automated: yes — `FdroidAntiFeaturesTest`
- Coverage: none declared

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
