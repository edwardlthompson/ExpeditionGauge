# Feature: aaos-standalone

> Optional AAOS sideload APK uses a distinct applicationId and requires `type.automotive`.

## Acceptance criteria

- ✅ Default phone APK stays `dev.foss.expeditiongauge` (automotive feature still soft)
- ✅ `-PaaosStandalone=true` suffixes `.aaos` and sets automotive required
- ✅ i18n: none (build identity only)

## Smoke scenario

1. Given `./gradlew :app:assembleDebug -PaaosStandalone=true`
2. When the APK is sideloaded on an AAOS unit
3. Then Package Manager accepts the automotive feature and the app id is `.aaos`

## Container map

| Layer | Path |
|-------|------|
| Logic | `aaosstandalone/AaosStandalone.kt` |
| View | none (build flag) |
| Tests | `app/src/test/.../aaosstandalone/` |
| Wiring | `app/build.gradle.kts` + manifest placeholder |

## Tests

- Automated: yes — `AaosStandaloneTest`
- Coverage: applicationId / version suffix; automotive required flag

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
