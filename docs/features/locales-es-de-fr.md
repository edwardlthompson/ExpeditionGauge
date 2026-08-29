# Feature: locales-es-de-fr

> Ship Spanish, German, and French strings for About, Live, Maps, and feedback.

## Acceptance criteria

- ✅ `values-es`, `values-de`, `values-fr` exist
- ✅ Keys: about_title, live_banner, settings_maps_heading, feedback_report_bug
- ✅ i18n: those keys translated

## Smoke scenario

1. Given the device locale is de
2. When About opens
3. Then the title is Über

## Container map

| Layer | Path |
|-------|------|
| Logic | `localesesdefr/LocalesEsDeFr.kt` |
| Resources | `res/values-{es,de,fr}/strings_i18n.xml` |
| Tests | `app/src/test/.../localesesdefr/` |

## Tests

- Automated: yes — `LocalesEsDeFrTest`
- Coverage: supported tags

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
