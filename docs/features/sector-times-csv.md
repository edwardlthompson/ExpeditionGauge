# Feature: sector-times-csv

> Export sector split times as CSV.

## Acceptance criteria

- ✅ Header is `lapId,sectorIndex,splitMs,sampleId`
- ✅ One row per sector split
- ✅ Playback can share the CSV
- ✅ i18n: `sector_times_csv_share`

## Smoke scenario

1. Given a session with sector splits
2. When Share sector times is tapped
3. Then a CSV file is offered to the share sheet

## Container map

| Layer | Path |
|-------|------|
| Logic | `sectortimescsv/SectorTimesCsv.kt` |
| View | `ui/sectortimescsv/SectorTimesShareButton.kt` |
| Tests | `app/src/test/.../sectortimescsv/` |

## Tests

- Automated: yes — `SectorTimesCsvTest`
- Coverage: header + row formatting

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
