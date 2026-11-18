# OBDex DTC titles (CC0)

English diagnostic trouble code titles shipped in `obdex_en_*.json`.

| | |
|--|--|
| Upstream | [foerbsnavi/OBDex](https://github.com/foerbsnavi/OBDex) |
| Data license | [CC0-1.0](https://github.com/foerbsnavi/OBDex/blob/main/LICENSE-DATA) (`LICENSE-DATA` in that repo) |
| What we vendor | Slim `code → English title` map parsed from `data/generic/*_enriched.yaml` |
| What we do **not** vendor | [OBDForge](https://github.com/edwardlthompson/OBDForge) sources (GPL-3.0-or-later) |
| Regen | `python3 scripts/expedition/fetch-obdex-dtc.py` |

Shards stay under the 500 KB tracked-file hygiene budget so the listing is plain JSON (F-Droid scanner can read it; no `scanignore` gzip).
