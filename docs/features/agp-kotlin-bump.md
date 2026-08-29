# Feature: agp-kotlin-bump

> Validate the KB-026 AGP/Kotlin pin. Do **not** bump AGP 9.2.1 / Kotlin 2.4.0.

## Acceptance criteria

- ✅ Current pin is AGP 9.2.1 and Kotlin 2.4.0
- ✅ Automerge skip covers `com.android.*` and `org.jetbrains.kotlin*`
- ✅ AGP 9.3.x / Kotlin 2.4.10 stays rejected
- ✅ i18n: none (toolchain pin)

## Smoke scenario

1. Given `examples/android/build.gradle.kts`
2. When Dependabot offers AGP 9.3.1
3. Then automerge is skipped per KB-026

## Container map

| Layer | Path |
|-------|------|
| Logic | `agpkotlinbump/AgpKotlinPin.kt` |
| Tests | `app/src/test/.../agpkotlinbump/` |
| Wiring | `.github/workflows/dependabot-automerge.yml` (existing skip) |

## Tests

- Automated: yes — `AgpKotlinPinTest`
- Coverage: pin hold; automerge skip

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`

## Notes

KB-026: AGP 9.3.x + Kotlin 2.4.10 broke `:app:processDebugNavigationResources` and CodeQL. Hold until a trial branch passes local+CI+CodeQL.
