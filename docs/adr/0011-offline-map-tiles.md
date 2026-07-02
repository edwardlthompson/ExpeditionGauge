# ADR-0011: Offline map tile prefetch

- **Status:** Accepted
- **Date:** 2026-06-30

## Context

Playback used online-only MapLibre demo tiles; offline trips showed a dark basemap with only the route line.

## Decision

1. **MapLibre Android `OfflineManager`** with FOSS style `https://demotiles.maplibre.org/style.json` (same URI online and offline).
2. **Home region** stored in DataStore (`HomeMapRegionPreferences`); user sets via Settings → Offline maps → use current GPS.
3. **`MapTilePrefetchWorker`** (WorkManager): home prefetch on **Wi‑Fi** (`UNMETERED`); session bbox enqueue after recording stops.
4. **Playback prompt** when tiles missing: wait for Wi‑Fi or explicit **Use cellular** (persists opt-in in `MapTileCacheRepository`).
5. **Cache index** in DataStore (`MapTileCacheRepository`) for fast bbox coverage checks.

## Consequences

- Demo tiles are suitable for development; production may switch to self-hosted FOSS vector tiles without changing architecture.
- Storage cap tuning deferred; monitor cache size in a follow-up.

## Privacy

Downloads are user-initiated or Wi‑Fi background prefetch only; no cellular without explicit consent.
