# Feature: live-receiver-record

> Keep a local ring of live receiver samples so Relive can play the pit feed.

## Acceptance criteria

- ✅ DTO maps onto `SampleEntity`
- ✅ Buffer caps at 6000 samples
- ✅ Receiver remembers each metric
- ✅ i18n: none

## Smoke scenario

1. Given a connected live receiver
2. When metrics arrive
3. Then `LiveReceiverRecord.snapshot()` has those samples

## Container map

| Layer | Path |
|-------|------|
| Logic | `livereceiverrecord/LiveReceiverRecord.kt` |
| Tests | `app/src/test/.../livereceiverrecord/` |
| Wiring | `LiveTelemetryReceiver.onMetricReceived` |

## Tests

- Automated: yes — `LiveReceiverRecordTest`
- Coverage: map; cap

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
