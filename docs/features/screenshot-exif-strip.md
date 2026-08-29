# Feature: screenshot-exif-strip

> Strip MediaStore GPS keys and detect JPEG EXIF GPS IFD before HUD screenshot insert.

## Acceptance criteria

- ✅ `latitude` / `longitude` keys are dropped from MediaStore values
- ✅ JPEG bytes with EXIF GPS IFD (`0x8825`) or GPS ASCII are flagged
- ✅ `HudScreenshotIo.insertBitmap` sanitizes `ContentValues`
- ✅ i18n: silent strip (no new strings)

## Smoke scenario

1. Given a screenshot insert with leftover latitude/longitude keys
2. When `stripLocation` runs
3. Then those keys are gone and a GPS EXIF JPEG is detected

## Container map

| Layer | Path |
|-------|------|
| Logic | `screenshotexifstrip/ScreenshotExifStrip.kt` |
| Tests | `app/src/test/.../screenshotexifstrip/` |
| Wiring | `media/HudScreenshotIo.insertBitmap` |

## Tests

- Automated: yes — `ScreenshotExifStripTest`
- Coverage: GPS key drop; IFD/ASCII detect

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
