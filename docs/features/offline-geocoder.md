# Feature: offline-geocoder

> Suggest a session title from a local place list when GPS is near a known track.

## Acceptance criteria

- ✅ Nearest place within 20 km wins
- ✅ Far or missing coordinates keep the fallback title
- ✅ Settings maps show the geocoded home-region name
- ✅ i18n: none (place names)

## Smoke scenario

1. Given home region at Pacific Raceways
2. When Settings maps opens
3. Then the geocoder label is Pacific Raceways

## Container map

| Layer | Path |
|-------|------|
| Logic | `offlinegeocoder/OfflineGeocoder.kt` |
| Tests | `app/src/test/.../offlinegeocoder/` |
| Wiring | `SettingsMapOptions` |

## Tests

- Automated: yes — `OfflineGeocoderTest`
- Coverage: hit, miss, null coords

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
