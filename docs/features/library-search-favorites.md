# Feature: library-search-favorites

> Library search stays on notes/tags; sessions can be starred and sort first.

## Acceptance criteria

- ✅ Existing search still matches notes, driver, tags, name
- ✅ Star toggle persists in `SessionFavoritesStore`
- ✅ Favorites appear first in the library list
- ✅ i18n: `session_favorite_on` / `session_favorite_off`

## Smoke scenario

1. Given two sessions in the library
2. When the user stars the older one
3. Then that session sorts to the top

## Container map

| Layer | Path |
|-------|------|
| Logic | `sessionfavorites/SessionFavorites.kt` |
| Store | `settings/SessionFavoritesStore.kt` |
| View | `ui/sessionfavorites/SessionFavoriteToggle.kt` |
| Tests | `app/src/test/.../sessionfavorites/` |
| Wiring | `SessionListScreen` |

## Tests

- Automated: yes — `SessionFavoritesTest`
- Coverage: parse/toggle; favorites-first sort

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
