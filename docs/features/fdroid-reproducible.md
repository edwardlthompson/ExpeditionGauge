# Feature: fdroid-reproducible

> Complete F-Droid listing files and pin `SOURCE_DATE_EPOCH` for reproducible publish.

## Acceptance criteria

- ✅ `license.txt` and `source_code.txt` are present
- ✅ `SOURCE_DATE_EPOCH=1700000000` is the documented pin
- ✅ Verify script checks those listing files
- ✅ i18n: store listing English only

## Smoke scenario

1. Given `examples/android/metadata/en-US/`
2. When `scripts/verify-fdroid-metadata.sh` runs
3. Then license, source URL, and changelog for the current versionCode pass

## Container map

| Layer | Path |
|-------|------|
| Logic | `fdroidreproducible/FdroidReproducible.kt` |
| Tests | `app/src/test/.../fdroidreproducible/` |
| Wiring | `scripts/verify-fdroid-metadata.sh` |

## Tests

- Automated: yes — `FdroidReproducibleTest` plus verify script
- Coverage: required files; epoch pin

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
