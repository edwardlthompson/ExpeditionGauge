# Feature: fastlane-next-changelog

> Fastlane/F-Droid changelog for the next versionCode (53).

## Acceptance criteria

- ✅ `metadata/en-US/changelogs/53.txt` exists
- ✅ Fastlane mirror has the same file
- ✅ Next code is current 52 + 1
- ✅ i18n: store listing English only

## Smoke scenario

1. Given versionCode 52 on the app
2. When the next listing is prepared
3. Then changelog 53 is present in metadata and Fastlane

## Container map

| Layer | Path |
|-------|------|
| Logic | `fastlanenextchangelog/FastlaneNextChangelog.kt` |
| Tests | `app/src/test/.../fastlanenextchangelog/` |
| Wiring | `metadata/en-US/changelogs/53.txt` |

## Tests

- Automated: yes — `FastlaneNextChangelogTest`
- Coverage: next versionCode

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
