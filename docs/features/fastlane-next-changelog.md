# Feature: fastlane-next-changelog

> Fastlane/F-Droid changelog for the next versionCode (54).

## Acceptance criteria

- ✅ `metadata/en-US/changelogs/54.txt` exists
- ✅ Fastlane mirror has the same file
- ✅ Next code is current 53 + 1
- ✅ i18n: store listing English only

## Smoke scenario

1. Given versionCode 53 on the app
2. When the next listing is prepared
3. Then changelog 54 is present in metadata and Fastlane

## Container map

| Layer | Path |
|-------|------|
| Logic | `fastlanenextchangelog/FastlaneNextChangelog.kt` |
| Tests | `app/src/test/.../fastlanenextchangelog/` |
| Wiring | `metadata/en-US/changelogs/54.txt` |

## Tests

- Automated: yes — `FastlaneNextChangelogTest`
- Coverage: next versionCode

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
