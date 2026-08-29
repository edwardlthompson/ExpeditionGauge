# Feature: about-oss-notices

> Show MIT / MapLibre / AndroidX notices on About.

## Acceptance criteria

- ✅ Completeness requires MIT, MapLibre, and AndroidX
- ✅ About shows the notice summary
- ✅ i18n: `about_oss_notices`

## Smoke scenario

1. Given About
2. When the screen is open
3. Then MIT · MapLibre · AndroidX is visible

## Container map

| Layer | Path |
|-------|------|
| Logic | `aboutossnotices/OssNotices.kt` |
| Tests | `app/src/test/.../aboutossnotices/` |
| Wiring | `AboutScreen` |

## Tests

- Automated: yes — `OssNoticesTest`
- Coverage: completeness; summary

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
