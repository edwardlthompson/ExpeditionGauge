# Feature: live-record-graphs

> Live sparkline of speed and latG while a session is recording.

## Acceptance criteria

- ✅ Recording HUD keeps a 30 s ring buffer of live samples
- ✅ Sparkline decimates to at most 120 points
- ✅ Dashboard live strip shows the graph without growing the composition root
- ✅ i18n: `live_record_graph_cd` in `strings_live_record_graphs.xml`

## Smoke scenario

1. Given Record is running
2. When speed or latG changes
3. Then the live strip draws a trailing sparkline

## Container map

| Layer | Path |
|-------|------|
| Logic | `liverecordgraphs/RecordingLiveGraph.kt` |
| View | `ui/recording/RecordingLiveGraphStrip.kt` |
| Tests | `app/src/test/.../liverecordgraphs/` |
| Wiring | `RecordingLiveStrip` + `DashboardScreen` (1 line) |

## Tests

- Automated: yes — `RecordingLiveGraphTest`
- Coverage: window eviction; decimate keeps ends; snapshot mapping

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
