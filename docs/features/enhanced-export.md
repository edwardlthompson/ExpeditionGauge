# Enhanced export

> Sprint 18 — GPX extensions, wide CSV, session bundles.

## Formats

| Format | Path | Notes |
|--------|------|-------|
| GPX 1.1 | `ExportService` | Extensions: drift, latG, slip, rpm, throttle, TPMS |
| CSV wide | `ExportService` | All sample columns + optional TPMS |
| JSON | `ExportService` | Nested metadata + samples |
| ZIP | `EnhancedExportService` | CSV + JSON + GPX + manifest + optional video |

## UI

- **Session list** — **Export ZIP bundle** on `RichSessionCard`
- **Playback** — burn-in MP4 when video linked

## Privacy

Local export only; share via Android intent.
